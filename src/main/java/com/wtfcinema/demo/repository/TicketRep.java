package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TicketRep extends JpaRepository<Ticket,Long> {
    public Optional<Ticket> findById(Long id);
    public void deleteById(Long id);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.snacks WHERE t.id = :id")
    public Optional<Ticket> findByIdWithSnack(Long id);
}
