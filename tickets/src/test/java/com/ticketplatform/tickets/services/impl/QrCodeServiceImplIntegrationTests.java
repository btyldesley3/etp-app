package com.ticketplatform.tickets.services.impl;


import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;


import com.ticketplatform.tickets.config.TestSecurityConfig;
import com.ticketplatform.tickets.domain.entities.*;
import com.ticketplatform.tickets.exceptions.QrCodeGenerationException;
import com.ticketplatform.tickets.exceptions.QrCodeNotFoundException;
import com.ticketplatform.tickets.repositories.QrCodeRepository;
import com.ticketplatform.tickets.repositories.TicketRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class QrCodeServiceImplIntegrationTest {

    @Autowired
    private QrCodeServiceImpl qrCodeService;

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;


    private User purchaser;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        // Persist a purchaser
        purchaser = new User();
        purchaser.setName("Test User");
        purchaser.setEmail("user@example.com");
        purchaser.setId(UUID.randomUUID());
        purchaser = userRepository.saveAndFlush(purchaser);

        // Persist a ticket
        ticket = new Ticket();
//        ticket.setId(purchaser.getId());
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setPurchaser(purchaser);
        ticket = ticketRepository.saveAndFlush(ticket);
    }

    // ------------------- generateQrCode -------------------

    @Test
    void generateQrCode_ShouldPersistQrCode() {
        QrCode qrCode = qrCodeService.generateQrCode(ticket);

        assertThat(qrCode.getId()).isNotNull();
        assertThat(qrCode.getTicket()).isEqualTo(ticket);
        assertThat(qrCode.getStatus()).isEqualTo(QrCodeStatusEnum.ACTIVE);
        assertThat(qrCode.getValue()).isNotEmpty();

        // Verify persistence
        QrCode persisted = qrCodeRepository.findById(qrCode.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted.getTicket()).isEqualTo(ticket);
    }

    @Test
    void generateQrCode_ShouldThrowQrCodeGenerationException_WhenWriterFails() throws WriterException {
        QRCodeWriter mockWriter = mock(QRCodeWriter.class);
        when(mockWriter.encode(anyString(), any(), anyInt(), anyInt()))
                .thenThrow(new com.google.zxing.WriterException("Test failure"));

        QrCodeServiceImpl failingService = new QrCodeServiceImpl(mockWriter, qrCodeRepository);

        assertThatThrownBy(() -> failingService.generateQrCode(ticket))
                .isInstanceOf(QrCodeGenerationException.class)
                .hasMessageContaining("Failed to generate QR Code");
    }

    // ------------------- getQrCodeImageForUserAndTicket -------------------

    @Test
    void getQrCodeImageForUserAndTicket_ShouldReturnDecodedImage() throws IOException {
        QrCode qrCode = qrCodeService.generateQrCode(ticket);

        byte[] imageBytes = qrCodeService.getQrCodeImageForUserAndTicket(purchaser.getId(), ticket.getId());

        assertThat(imageBytes).isNotEmpty();

        // Verify valid PNG image
        assertThat(ImageIO.read(new ByteArrayInputStream(imageBytes))).isNotNull();
    }

    @Test
    void getQrCodeImageForUserAndTicket_ShouldThrow_WhenQrCodeNotFound() {
        UUID fakeTicketId = UUID.randomUUID();

        assertThatThrownBy(() -> qrCodeService.getQrCodeImageForUserAndTicket(purchaser.getId(), fakeTicketId))
                .isInstanceOf(QrCodeNotFoundException.class);
    }

    @Test
    void getQrCodeImageForUserAndTicket_ShouldThrow_WhenQrCodeInvalidBase64() {
        QrCode qrCode = new QrCode();
        qrCode.setId(UUID.randomUUID());
        qrCode.setTicket(ticket);
        qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
        qrCode.setValue("INVALID_BASE64"); // deliberately invalid
        qrCodeRepository.saveAndFlush(qrCode);

        assertThatThrownBy(() -> qrCodeService.getQrCodeImageForUserAndTicket(purchaser.getId(), ticket.getId()))
                .isInstanceOf(QrCodeNotFoundException.class);
    }
}