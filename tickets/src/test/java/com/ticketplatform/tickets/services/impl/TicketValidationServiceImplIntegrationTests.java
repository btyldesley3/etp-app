package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.config.TestSecurityConfig;
import com.ticketplatform.tickets.domain.entities.*;
import com.ticketplatform.tickets.exceptions.QrCodeNotFoundException;
import com.ticketplatform.tickets.exceptions.TicketNotFoundException;
import com.ticketplatform.tickets.repositories.QrCodeRepository;
import com.ticketplatform.tickets.repositories.TicketRepository;
import com.ticketplatform.tickets.repositories.TicketValidationRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(TestSecurityConfig.class)
class TicketValidationServiceImplIntegrationTest {

    @Autowired
    private TicketValidationServiceImpl ticketValidationService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketValidationRepository ticketValidationRepository;

    private Ticket ticket;
    private User user;
    private QrCode qrCode;

    @BeforeEach
    void setUp() {
        // Create a user
        user = new User();
        user.setName("Test User");
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user = userRepository.saveAndFlush(user);

        // Create a ticket
        ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setPurchaser(user);
        ticket = ticketRepository.saveAndFlush(ticket);

        // Create a QR code
        qrCode = new QrCode();
        qrCode.setId(UUID.randomUUID());
        qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
        qrCode.setTicket(ticket);
        qrCode.setValue("fakeBase64Value");
        qrCode = qrCodeRepository.saveAndFlush(qrCode);
    }

    // ------------------- validateTicketByQrCode -------------------

    @Test
    void validateTicketByQrCode_ShouldReturnValidTicketValidation() {
        TicketValidation validation = ticketValidationService.validateTicketByQrCode(qrCode.getId());

        assertThat(validation).isNotNull();
        assertThat(validation.getTicket()).isEqualTo(ticket);
        assertThat(validation.getValidationMethod()).isEqualTo(TicketValidationMethodEnum.QR_SCAN);
        assertThat(validation.getValidationStatus()).isEqualTo(TicketValidationStatusEnum.VALID);
    }

    @Test
    void validateTicketByQrCode_ShouldThrow_WhenQrCodeNotFound() {
        UUID fakeQrCodeId = UUID.randomUUID();
        assertThatThrownBy(() -> ticketValidationService.validateTicketByQrCode(fakeQrCodeId))
                .isInstanceOf(QrCodeNotFoundException.class)
                .hasMessageContaining("QR Code with ID");
    }

    @Test
    void validateTicketByQrCode_ShouldMarkAsInvalid_WhenPreviousValidationWasValid() {
        // First validation: should be VALID
        TicketValidation first = ticketValidationService.validateTicketByQrCode(qrCode.getId());
        assertThat(first.getValidationStatus()).isEqualTo(TicketValidationStatusEnum.VALID);

        // Second validation: should toggle to INVALID
        TicketValidation second = ticketValidationService.validateTicketByQrCode(qrCode.getId());
        assertThat(second.getValidationStatus()).isEqualTo(TicketValidationStatusEnum.INVALID);
    }

    // ------------------- validateTicketManually -------------------

    @Test
    void validateTicketManually_ShouldReturnValidTicketValidation() {
        TicketValidation validation = ticketValidationService.validateTicketManually(ticket.getId());

        assertThat(validation).isNotNull();
        assertThat(validation.getTicket()).isEqualTo(ticket);
        assertThat(validation.getValidationMethod()).isEqualTo(TicketValidationMethodEnum.MANUAL);
        assertThat(validation.getValidationStatus()).isEqualTo(TicketValidationStatusEnum.VALID);
    }

    @Test
    void validateTicketManually_ShouldThrow_WhenTicketNotFound() {
        UUID fakeTicketId = UUID.randomUUID();
        assertThatThrownBy(() -> ticketValidationService.validateTicketManually(fakeTicketId))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void validateTicketManually_ShouldMarkAsInvalid_WhenPreviousValidationWasValid() {
        // First manual validation
        TicketValidation first = ticketValidationService.validateTicketManually(ticket.getId());
        assertThat(first.getValidationStatus()).isEqualTo(TicketValidationStatusEnum.VALID);


        // Second manual validation: should be INVALID
        TicketValidation second = ticketValidationService.validateTicketManually(ticket.getId());
        assertThat(second.getValidationStatus()).isEqualTo(TicketValidationStatusEnum.INVALID);
    }
}