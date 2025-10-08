package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.config.TestSecurityConfig;
import com.ticketplatform.tickets.domain.entities.Ticket;
import com.ticketplatform.tickets.domain.entities.TicketStatusEnum;
import com.ticketplatform.tickets.domain.entities.User;
import com.ticketplatform.tickets.repositories.TicketRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import com.ticketplatform.tickets.services.TicketService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
@Transactional
@Import(TestSecurityConfig.class)
class TicketServiceImplIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        // Persist a user
        user = new User();
        user.setName("Test User");
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user = userRepository.saveAndFlush(user);

        // Persist a ticket for the user
        ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setPurchaser(user);
        ticket = ticketRepository.saveAndFlush(ticket);
    }

    // ------------------- listTicketsForUser -------------------

    @Test
    void listTicketsForUser_ShouldReturnTicketsForUser() {
        Page<Ticket> page = ticketService.listTicketsForUser(user.getId(), PageRequest.of(0, 10));

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0)).isEqualTo(ticket);
    }

    @Test
    void listTicketsForUser_ShouldReturnEmptyPage_WhenNoTicketsExist() {
        UUID otherUserId = UUID.randomUUID();
        Page<Ticket> page = ticketService.listTicketsForUser(otherUserId, PageRequest.of(0, 10));

        assertThat(page).isNotNull();
        assertThat(page.getContent()).isEmpty();
    }

    // ------------------- getTicketForUser -------------------

    @Test
    void getTicketForUser_ShouldReturnTicket() {
        Optional<Ticket> result = ticketService.getTicketForUser(user.getId(), ticket.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(ticket);
    }

    @Test
    void getTicketForUser_ShouldReturnEmpty_WhenTicketDoesNotBelongToUser() {
        UUID otherUserId = UUID.randomUUID();
        Optional<Ticket> result = ticketService.getTicketForUser(otherUserId, ticket.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getTicketForUser_ShouldReturnEmpty_WhenTicketDoesNotExist() {
        UUID fakeTicketId = UUID.randomUUID();
        Optional<Ticket> result = ticketService.getTicketForUser(user.getId(), fakeTicketId);

        assertThat(result).isEmpty();
    }
}