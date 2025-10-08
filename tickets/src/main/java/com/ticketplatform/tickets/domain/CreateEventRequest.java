package com.ticketplatform.tickets.domain;

import com.ticketplatform.tickets.domain.entities.EventStatusEnum;
import com.ticketplatform.tickets.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {
    private String name;
    private LocalDateTime startDateAndTime;
    private LocalDateTime endDateAndTime;
    private String venue;
    private LocalDateTime saleStartDateAndTime;
    private LocalDateTime saleEndDateAndTime;
    private EventStatusEnum status;
    private List<CreateTicketTypeRequest> ticketTypes = new ArrayList<>();


}
