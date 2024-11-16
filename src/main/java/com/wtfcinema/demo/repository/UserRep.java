package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRep extends JpaRepository<User,Long> {
    public Optional<User> findById(Long id);

    public Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tickets WHERE u.id = :userId")
    Optional<User> findByIdWithTickets(Long userId);
}

