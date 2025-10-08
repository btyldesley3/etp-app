package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.config.TestSecurityConfig;
import com.ticketplatform.tickets.domain.CreateEventRequest;
import com.ticketplatform.tickets.domain.CreateTicketTypeRequest;
import com.ticketplatform.tickets.domain.UpdateEventRequest;
import com.ticketplatform.tickets.domain.UpdateTicketTypeRequest;
import com.ticketplatform.tickets.domain.entities.Event;
import com.ticketplatform.tickets.domain.entities.EventStatusEnum;
import com.ticketplatform.tickets.domain.entities.User;
import com.ticketplatform.tickets.exceptions.EventOrganizerAccessException;
import com.ticketplatform.tickets.exceptions.EventUpdateException;
import com.ticketplatform.tickets.exceptions.UserNotFoundException;
import com.ticketplatform.tickets.repositories.EventRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import com.ticketplatform.tickets.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

// Full application context with H2
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@Import(TestSecurityConfig.class)
class EventServiceImplIntegrationTest {

    //Some data fields are non-null in production and therefore required as part of test cases

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    private User organizer;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(UUID.randomUUID());
        organizer.setName("Alice");
        organizer.setEmail("AliceInWonderland@fairytale.com");
        organizer = userRepository.save(organizer);
    }

    // ------------------- createEvent -------------------

    @Test
    void createEvent_ShouldPersistEventWithTickets() {
        CreateEventRequest request = new CreateEventRequest();
        request.setName("Concert");
        request.setVenue("Stadium");
        request.setStartDateAndTime(LocalDateTime.now().plusDays(1));
        request.setEndDateAndTime(LocalDateTime.now().plusDays(2));
        request.setSaleStartDateAndTime(LocalDateTime.now());
        request.setSaleEndDateAndTime(LocalDateTime.now().plusDays(1));
        request.setStatus(EventStatusEnum.DRAFT);
        request.setTicketTypes(List.of(
                new CreateTicketTypeRequest("VIP", 100.00, "Front row", 50)
        ));

        Event saved = eventService.createEvent(organizer.getId(), request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTicketTypes()).hasSize(1);

        Optional<Event> reloaded = eventRepository.findById(saved.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getTicketTypes()).hasSize(1);
    }

    @Test
    void createEvent_ShouldFail_WhenOrganizerDoesNotExist() {
        UUID fakeId = UUID.randomUUID();
        CreateEventRequest request = new CreateEventRequest();

        assertThatThrownBy(() -> eventService.createEvent(fakeId, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    // ------------------- listEventsForOrganizer -------------------

    @Test
    void listEventsForOrganizer_ShouldReturnEvents() {
        // Seed one event
        Event event = new Event();
        event.setName("Seeded");
        event.setVenue("A very big venue");
        event.setStatus(EventStatusEnum.PUBLISHED);
        event.setOrganizer(organizer);
        eventRepository.save(event);

        Page<Event> result = eventService.listEventsForOrganizer(organizer.getId(), PageRequest.of(0, 5));

        assertThat(result.getContent()).extracting(Event::getName).contains("Seeded");
    }

    // ------------------- updateEventForOrganizer -------------------

    @Test
    void updateEventForOrganizer_ShouldUpdateFieldsAndAddTicketType() {
        Event event = new Event();
        event.setName("Old name");
        event.setVenue("A very big venue");
        event.setStatus(EventStatusEnum.PUBLISHED);
        event.setOrganizer(organizer);
        event = eventRepository.save(event);

        UpdateEventRequest request = new UpdateEventRequest();
        request.setId(event.getId());
        request.setName("New name");
        request.setTicketTypes(List.of(
                new UpdateTicketTypeRequest(null, "Standard", 10.00, "desc", 100)
        ));

        Event updated = eventService.updateEventForOrganizer(organizer.getId(), event.getId(), request);

        assertThat(updated.getName()).isEqualTo("New name");
        assertThat(updated.getTicketTypes()).hasSize(1);
    }

    @Test
    void updateEventForOrganizer_ShouldFail_WhenIdsMismatch() {
        Event event = new Event();
        event.setName("Event");
        event.setVenue("A very big venue");
        event.setStatus(EventStatusEnum.PUBLISHED);
        event.setOrganizer(organizer);
        event = eventRepository.save(event);

        UpdateEventRequest request = new UpdateEventRequest();
        request.setId(UUID.randomUUID());

        Event finalEvent = event;
        assertThatThrownBy(() -> eventService.updateEventForOrganizer(organizer.getId(), finalEvent.getId(), request))
                .isInstanceOf(EventUpdateException.class);
    }

    // ------------------- deleteEventForOrganizer -------------------

    @Test
    void deleteEventForOrganizer_ShouldDeleteEvent() {
        Event event = new Event();
        event.setName("ToDelete");
        event.setVenue("A very big venue");
        event.setStatus(EventStatusEnum.PUBLISHED);
        event.setOrganizer(organizer);
        event = eventRepository.save(event);

        eventService.deleteEventForOrganizer(organizer.getId(), event.getId());

        assertThat(eventRepository.findById(event.getId())).isEmpty();
    }

    @Test
    void deleteEventForOrganizer_ShouldFail_WhenEventDoesNotBelongToOrganizer() {
        User otherOrganizer = new User();
        otherOrganizer.setId(UUID.randomUUID());
        otherOrganizer.setName("Other Organizer");
        otherOrganizer.setEmail("OrganizerAccessDenied@exception.com");
        otherOrganizer = userRepository.save(otherOrganizer);
        Event event = new Event();
        event.setName("Not yours");
        event.setVenue("A very big venue");
        event.setStatus(EventStatusEnum.PUBLISHED);
        event.setOrganizer(otherOrganizer); // different organizer
        eventRepository.save(event);


        assertThatThrownBy(() -> eventService.deleteEventForOrganizer(organizer.getId(), event.getId()))
                .isInstanceOf(EventOrganizerAccessException.class);
    }

    // ------------------- published events -------------------

    @Test
    void listPublishedEvents_ShouldReturnOnlyPublished() {
        Event published = new Event();
        published.setName("Pub");
        published.setVenue("A very big venue");
        published.setStatus(EventStatusEnum.PUBLISHED);
        published.setOrganizer(organizer);
        eventRepository.save(published);

        Event draft = new Event();
        draft.setName("Draft");
        draft.setVenue("A very small venue");
        draft.setStatus(EventStatusEnum.DRAFT);
        draft.setOrganizer(organizer);
        eventRepository.save(draft);

        Page<Event> result = eventService.listPublishedEvents(PageRequest.of(0, 5));

        assertThat(result.getContent()).extracting(Event::getStatus)
                .containsOnly(EventStatusEnum.PUBLISHED);
    }
}
