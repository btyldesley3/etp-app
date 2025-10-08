package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.config.TestSecurityConfig;
import com.ticketplatform.tickets.domain.entities.Ticket;
import com.ticketplatform.tickets.domain.entities.TicketStatusEnum;
import com.ticketplatform.tickets.domain.entities.TicketType;
import com.ticketplatform.tickets.domain.entities.User;
import com.ticketplatform.tickets.exceptions.TicketsSoldOutException;
import com.ticketplatform.tickets.repositories.TicketRepository;
import com.ticketplatform.tickets.repositories.TicketTypeRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import com.ticketplatform.tickets.services.QrCodeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@Import(TestSecurityConfig.class)
class TicketTypeServiceImplIntegrationTest {

    @Autowired
    private TicketTypeServiceImpl ticketTypeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private QrCodeService qrCodeService;

    private User user;
    private TicketType ticketType;

    @BeforeEach
    void setUp() {
        // Persist a user
        user = new User();
        user.setName("Test User");
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user = userRepository.saveAndFlush(user);

        // Persist a ticket type
        ticketType = new TicketType();
        ticketType.setName("VIP");
        ticketType.setPrice(100.0);
        ticketType.setDescription("VIP Ticket");
        ticketType.setTotalTicketsAvailable(2);
        ticketType = ticketTypeRepository.saveAndFlush(ticketType);
    }

    // ------------------- purchaseTicket -------------------

    @Test
    void purchaseTicket_ShouldCreateTicketAndGenerateQrCode() {
        Ticket ticket = ticketTypeService.purchaseTicket(user.getId(), ticketType.getId());

        assertThat(ticket.getId()).isNotNull();
        assertThat(ticket.getPurchaser()).isEqualTo(user);
        assertThat(ticket.getTicketType()).isEqualTo(ticketType);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatusEnum.PURCHASED);

        // Verify it is persisted
        Ticket persisted = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(persisted).isEqualTo(ticket);
    }

    @Test
    void purchaseTicket_ShouldThrow_WhenTicketsSoldOut() {
        // Purchase all available tickets
        ticketTypeService.purchaseTicket(user.getId(), ticketType.getId());
        ticketTypeService.purchaseTicket(user.getId(), ticketType.getId());

        // Next purchase should fail
        assertThatThrownBy(() -> ticketTypeService.purchaseTicket(user.getId(), ticketType.getId()))
                .isInstanceOf(TicketsSoldOutException.class);
    }

    @Test
    void purchaseTicket_ShouldThrow_WhenUserNotFound() {
        UUID fakeUserId = UUID.randomUUID();

        assertThatThrownBy(() -> ticketTypeService.purchaseTicket(fakeUserId, ticketType.getId()))
                .hasMessageContaining("User with ID")
                .isInstanceOf(RuntimeException.class); // your UserNotFoundException class
    }

    @Test
    void purchaseTicket_ShouldThrow_WhenTicketTypeNotFound() {
        UUID fakeTicketTypeId = UUID.randomUUID();

        assertThatThrownBy(() -> ticketTypeService.purchaseTicket(user.getId(), fakeTicketTypeId))
                .hasMessageContaining("Ticket type with ID")
                .isInstanceOf(RuntimeException.class); // your TicketTypeNotFoundException class
    }
}