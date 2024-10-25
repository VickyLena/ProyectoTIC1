package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRep extends JpaRepository<User,Long> {
    public Optional<User> findById(Long id);
}
