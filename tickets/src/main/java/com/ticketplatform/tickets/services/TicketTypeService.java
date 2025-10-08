package com.ticketplatform.tickets.services;

import com.ticketplatform.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface TicketTypeService {
    Ticket purchaseTicket(UUID userId, UUID ticketType);
}
