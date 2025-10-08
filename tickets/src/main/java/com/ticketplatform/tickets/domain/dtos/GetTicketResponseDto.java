package com.ticketplatform.tickets.domain.dtos;

import com.ticketplatform.tickets.domain.entities.TicketStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTicketResponseDto {
    private UUID id;
    private TicketStatusEnum status;
    private Double price;
    private String description;
    private String eventName;
    private String eventVenue;
    private LocalDateTime eventStartDateAndTime;
    private LocalDateTime eventEndDateAndTime;

}
