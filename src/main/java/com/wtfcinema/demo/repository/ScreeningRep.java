package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.entities.User;
import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ScreeningRep extends JpaRepository<Screening,Long> {
    public Optional<Screening> findById(Long id);

    @Query("SELECT ts FROM Screening ts LEFT JOIN FETCH ts.takenSeats WHERE ts.id = :screeningId")
    Optional<Screening> findByIdWithTickets(Long screeningId);

}
