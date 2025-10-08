package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.domain.CreateEventRequest;
import com.ticketplatform.tickets.domain.UpdateEventRequest;
import com.ticketplatform.tickets.domain.UpdateTicketTypeRequest;
import com.ticketplatform.tickets.domain.entities.Event;
import com.ticketplatform.tickets.domain.entities.EventStatusEnum;
import com.ticketplatform.tickets.domain.entities.TicketType;
import com.ticketplatform.tickets.domain.entities.User;
import com.ticketplatform.tickets.exceptions.*;
import com.ticketplatform.tickets.repositories.EventRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import com.ticketplatform.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest event) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID: '%s' not found", organizerId))
                );

        Event eventToCreate = new Event();

        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(ticketType -> {
            TicketType ticketTypeToCreate = new TicketType();
            ticketTypeToCreate.setName(ticketType.getName());
            ticketTypeToCreate.setPrice(ticketType.getPrice());
            ticketTypeToCreate.setDescription(ticketType.getDescription());
            ticketTypeToCreate.setTotalTicketsAvailable(ticketType.getTotalTicketsAvailable());
            ticketTypeToCreate.setEvent(eventToCreate);
            return ticketTypeToCreate;
        }).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStartDateAndTime(event.getStartDateAndTime());
        eventToCreate.setEndDateAndTime(event.getEndDateAndTime());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSaleStartDateAndTime(event.getSaleStartDateAndTime());
        eventToCreate.setSaleEndDateAndTime(event.getSaleStartDateAndTime());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setOrganizer(organizer);
        eventToCreate.setTicketTypes(ticketTypesToCreate);
        return eventRepository.save(eventToCreate);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerID, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerID, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if (event.getId() == null) {
            throw new EventUpdateException("Event ID cannot be null");
        }

        if (!id.equals(event.getId())) {
            throw new EventUpdateException("Cannot update the ID of an event");
        }

        Event existingEvent = eventRepository
                .findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID '%s' does not exist", id))
                );

        existingEvent.setName(event.getName());
        existingEvent.setStartDateAndTime(event.getStartDateAndTime());
        existingEvent.setEndDateAndTime(event.getEndDateAndTime());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSaleStartDateAndTime(event.getSaleStartDateAndTime());
        existingEvent.setSaleEndDateAndTime(event.getSaleEndDateAndTime());
        existingEvent.setStatus(event.getStatus());

        //Get List of ticket type ids, excluding nulls and return as a Set<UUID>
        Set<UUID> requestTicketTypeIds = event.getTicketTypes().stream().map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        //Remove ids from Set that are not on the existing event
        existingEvent.getTicketTypes().removeIf(existingTicketType ->
                !requestTicketTypeIds.contains(existingTicketType.getId())
        );
        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));


        for (UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if (null == ticketType.getId()) {
                //Create new ticket type
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketType.getName());
                ticketTypeToCreate.setPrice(ticketType.getPrice());
                ticketTypeToCreate.setDescription(ticketType.getDescription());
                ticketTypeToCreate.setTotalTicketsAvailable(ticketType.getTotalTicketsAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);


            } else if (existingTicketTypesIndex.containsKey(ticketType.getId())) {
                //Update existing ticket type
                TicketType existingTicketType = existingTicketTypesIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalTicketsAvailable(ticketType.getTotalTicketsAvailable());

            } else {
                throw new TicketTypeNotFoundException(String.format(
                        "Ticket Type with ID '%s' does not exist", ticketType.getId()
                ));
            }
        }
        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
        if (getEventForOrganizer(organizerId, id).isEmpty()) {
            throw new EventOrganizerAccessException(String.format("Organizer ID: '%s' does not have permission to delete Event!", organizerId));
        } else {
            getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
        }
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvent(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }
}
