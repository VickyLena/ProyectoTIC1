package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheatreRep extends JpaRepository<Theatre,Long> {

    public Optional<Theatre> findById(Long id);
}
