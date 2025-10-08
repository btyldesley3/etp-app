package com.ticketplatform.tickets.services.impl;

import com.ticketplatform.tickets.domain.entities.Ticket;
import com.ticketplatform.tickets.domain.entities.TicketStatusEnum;
import com.ticketplatform.tickets.domain.entities.TicketType;
import com.ticketplatform.tickets.domain.entities.User;
import com.ticketplatform.tickets.exceptions.TicketsSoldOutException;
import com.ticketplatform.tickets.exceptions.TicketTypeNotFoundException;
import com.ticketplatform.tickets.exceptions.UserNotFoundException;
import com.ticketplatform.tickets.repositories.TicketRepository;
import com.ticketplatform.tickets.repositories.TicketTypeRepository;
import com.ticketplatform.tickets.repositories.UserRepository;
import com.ticketplatform.tickets.services.QrCodeService;
import com.ticketplatform.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;


    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s was not found", userId)
        ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() -> new TicketTypeNotFoundException(
                String.format("Ticket type with ID %s was not found", ticketTypeId)
        ));

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalTicketsAvailable = ticketType.getTotalTicketsAvailable();

        if(purchasedTickets + 1 > totalTicketsAvailable) {
            throw new TicketsSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);
        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);
        return ticketRepository.save(savedTicket);
    }
}
