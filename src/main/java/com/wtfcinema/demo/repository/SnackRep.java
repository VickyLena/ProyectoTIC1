package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Snack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnackRep extends JpaRepository<Snack,String> {
    public Optional<Snack> findById(String snackId);
    public Optional<Snack> findByName(String name);
    public void delete(Snack snack);
}
