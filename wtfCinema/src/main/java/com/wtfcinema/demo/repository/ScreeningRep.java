package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreeningRep extends JpaRepository<Screening,Long> {
    public Optional<Screening> findById(Long id);
}
