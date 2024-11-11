package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRep extends JpaRepository<Movie, Long> {
    public Optional<Movie> findById(Long id);

    public Optional<Movie> findByTitle(String title);

    List<Movie> findByAgeRestriction(int ageRestriction);

}

