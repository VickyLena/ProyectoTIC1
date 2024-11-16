package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRep extends JpaRepository<Movie, Long> {
    public Optional<Movie> findById(Long id);

    public Optional<Movie> findByTitle(String title);

    List<Movie> findByAgeRestriction(int ageRestriction);

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.genres")
    List<Movie> findAllWithGenres();

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.screenings WHERE m.id = :movieId")
    Optional<Movie> findByIdWithScreenings(@Param("movieId") Long movieId);
}

