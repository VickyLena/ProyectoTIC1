package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Ticket;
import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScreeningRep extends JpaRepository<Screening,Long> {
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.screenings WHERE m.id = :movieId")
    Optional<Movie> findByIdWithScreenings(@Param("movieId") Long movieId);

}
