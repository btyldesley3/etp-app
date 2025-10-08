package com.ticketplatform.tickets.exceptions;

public class EventOrganizerAccessException extends EventTicketException {
    public EventOrganizerAccessException() {
    }

    public EventOrganizerAccessException(String message) {
        super(message);
    }

    public EventOrganizerAccessException(Throwable cause) {
        super(cause);
    }

    public EventOrganizerAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventOrganizerAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
