package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CinemaRep extends JpaRepository<Cinema,String> {
    public Optional<Cinema> findByName(String name);
}
