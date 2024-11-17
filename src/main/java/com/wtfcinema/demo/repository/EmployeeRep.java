package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Employee;
import com.wtfcinema.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRep extends JpaRepository<Employee,Long> {
    public Optional<Employee> findById(Long id);

    public Optional<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.tickets WHERE e.email = :email")
    Optional<Employee> findByEmailWithTickets(String email);
}

