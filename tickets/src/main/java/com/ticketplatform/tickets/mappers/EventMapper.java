package com.ticketplatform.tickets.mappers;

import com.ticketplatform.tickets.domain.CreateEventRequest;
import com.ticketplatform.tickets.domain.CreateTicketTypeRequest;
import com.ticketplatform.tickets.domain.UpdateEventRequest;
import com.ticketplatform.tickets.domain.UpdateTicketTypeRequest;
import com.ticketplatform.tickets.domain.dtos.*;
import com.ticketplatform.tickets.domain.entities.Event;
import com.ticketplatform.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventResponseDto(Event event);

    GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto(TicketType ticketType);

    GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);

    UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);

    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

    UpdateEventResponseDto toUpdateEventResponseDto(Event event);

    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

    GetPublishedEventDetailsTicketTypesResponseDto toGetPublishedEventDetailsTicketTypesResponseDto(TicketType ticketType);

    GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);
}
