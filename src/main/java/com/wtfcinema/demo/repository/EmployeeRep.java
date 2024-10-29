package com.wtfcinema.demo.repository;

import com.wtfcinema.demo.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRep extends JpaRepository<Employee,Long> {
    public Optional<Employee> findById(Long id);

    public Optional<Employee> findByEmail(String email);
}

