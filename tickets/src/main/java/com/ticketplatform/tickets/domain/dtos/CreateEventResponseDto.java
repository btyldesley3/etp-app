package com.ticketplatform.tickets.domain.dtos;

import com.ticketplatform.tickets.domain.entities.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventResponseDto {
    private UUID id;

    private String name;

    private LocalDateTime startDateAndTime;

    private LocalDateTime endDateAndTime;

    private String venue;

    private LocalDateTime saleStartDateAndTime;

    private LocalDateTime saleEndDateAndTime;

    private EventStatusEnum status;

    private List<CreateTicketTypeResponseDto> ticketTypes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
