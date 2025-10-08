package com.ticketplatform.tickets.domain.dtos;

import com.ticketplatform.tickets.domain.entities.EventStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequestDto {

    @NotNull(message = "Event id must be provided")
    private UUID id;

    @NotBlank(message = "Event name is required")
    private String name;

    private LocalDateTime startDateAndTime;

    private LocalDateTime endDateAndTime;

    @NotBlank(message = "Venue information is required")
    private String venue;

    private LocalDateTime saleStartDateAndTime;

    private LocalDateTime saleEndDateAndTime;

    @NotNull(message = "Event status must be provided")
    private EventStatusEnum status;

    @NotEmpty(message = "At least one ticket type is required")
    @Valid
    private List<UpdateTicketTypeRequestDto> ticketTypes;

}
