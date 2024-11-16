package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Snack;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnackRep extends JpaRepository<Snack,Long> {
    public Optional<Snack> findById(Long snackId);
    public Optional<Snack> findByName(String name);

    @Transactional
    public void deleteById(Long id);
}
