package com.ticketplatform.tickets.services;

import com.ticketplatform.tickets.domain.entities.QrCode;
import com.ticketplatform.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);
    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
