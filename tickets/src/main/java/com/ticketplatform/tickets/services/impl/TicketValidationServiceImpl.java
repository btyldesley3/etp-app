package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.domain.entities.*;
import com.ticketplatform.tickets.exceptions.QrCodeNotFoundException;
import com.ticketplatform.tickets.exceptions.TicketNotFoundException;
import com.ticketplatform.tickets.repositories.QrCodeRepository;
import com.ticketplatform.tickets.repositories.TicketRepository;
import com.ticketplatform.tickets.repositories.TicketValidationRepository;
import com.ticketplatform.tickets.services.TicketValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketValidationServiceImpl implements TicketValidationService {

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final TicketRepository ticketRepository;

    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(String.format(
                        "QR Code with ID %s was not found", qrCodeId)
                ));
        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket, TicketValidationMethodEnum.QR_SCAN);
    }

    private TicketValidation validateTicket(Ticket ticket, TicketValidationMethodEnum ticketValidationMethodEnum) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(ticketValidationMethodEnum);

        TicketValidationStatusEnum ticketValidationStatus = ticket.getValidations().stream()
                .filter(v -> TicketValidationStatusEnum.VALID.equals(v.getValidationStatus()))
                .findFirst()
                .map(v -> TicketValidationStatusEnum.INVALID)
                .orElse(TicketValidationStatusEnum.VALID);
        ticketValidation.setValidationStatus(ticketValidationStatus);

        TicketValidation saved = ticketValidationRepository.save(ticketValidation);

        // Add to ticket's in-memory list so next validation sees it
        ticket.getValidations().add(saved);

        return saved;
    }

    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);
        return validateTicket(ticket, TicketValidationMethodEnum.MANUAL);
    }
}
