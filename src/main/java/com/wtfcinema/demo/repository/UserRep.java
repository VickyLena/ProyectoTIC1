package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRep extends JpaRepository<User,Long> {
    public Optional<User> findById(Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tickets WHERE u.email = :email")
    Optional<User> findByEmailWithTickets(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tickets WHERE u.id = :userId")
    Optional<User> findByIdWithTickets(Long userId);
}

