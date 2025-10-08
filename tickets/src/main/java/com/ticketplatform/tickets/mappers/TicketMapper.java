package com.ticketplatform.tickets.mappers;

import com.ticketplatform.tickets.domain.dtos.GetTicketResponseDto;
import com.ticketplatform.tickets.domain.dtos.ListTicketResponseDto;
import com.ticketplatform.tickets.domain.dtos.ListTicketTicketTypeResponseDto;
import com.ticketplatform.tickets.domain.entities.Ticket;
import com.ticketplatform.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {
    
    ListTicketTicketTypeResponseDto toListTicketTicketTypeResponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

    //Mapping being done by Mapstruct plugin
    @Mapping(target = "price", source = "ticket.ticketType.price")
    @Mapping(target = "description", source = "ticket.ticketType.description")
    @Mapping(target = "eventName", source = "ticket.ticketType.event.name")
    @Mapping(target = "eventVenue", source = "ticket.ticketType.event.venue")
    @Mapping(target = "eventStartDateAndTime", source = "ticket.ticketType.event.startDateAndTime")
    @Mapping(target = "eventEndDateAndTime", source = "ticket.ticketType.event.endDateAndTime")
    GetTicketResponseDto toGetTicketResponseDto(Ticket ticket);
}
