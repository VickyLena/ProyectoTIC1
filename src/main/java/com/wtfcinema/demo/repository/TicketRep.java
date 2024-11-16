package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRep extends JpaRepository<Ticket,Long> {
    public Optional<Ticket> findById(Long id);
    public void deleteById(Long id);
}
