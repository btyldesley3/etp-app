package com.ticketplatform.tickets.controllers;

import com.ticketplatform.tickets.domain.dtos.TicketValidationRequestDto;
import com.ticketplatform.tickets.domain.dtos.TicketValidationResponseDto;
import com.ticketplatform.tickets.domain.entities.TicketValidation;
import com.ticketplatform.tickets.domain.entities.TicketValidationMethodEnum;
import com.ticketplatform.tickets.mappers.TicketValidationMapper;
import com.ticketplatform.tickets.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(
            @RequestBody TicketValidationRequestDto ticketValidationRequestDto
    ) {
        TicketValidationMethodEnum method = ticketValidationRequestDto.getMethod();
        TicketValidation ticketValidation;
        if(TicketValidationMethodEnum.MANUAL.equals(method)) {
            ticketValidation = ticketValidationService.validateTicketManually(
                    ticketValidationRequestDto.getId());
        } else {
            ticketValidation = ticketValidationService.validateTicketByQrCode(
                   ticketValidationRequestDto.getId());
        }
        return ResponseEntity.ok(ticketValidationMapper.toTicketValidationResponseDto(ticketValidation));
    }

}
