package com.ticketplatform.tickets.repositories;

import com.ticketplatform.tickets.domain.entities.Ticket;
import com.ticketplatform.tickets.domain.entities.TicketType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {

    //Pessimistic lock applied to prevent inconsistent state in the database created by attempting to
    //purchase a ticket at the exact same time.
    @Query("SELECT tt FROM TicketType tt WHERE tt.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TicketType> findByIdWithLock(@Param("id")UUID id);
}
