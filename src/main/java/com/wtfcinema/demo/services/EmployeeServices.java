package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Employee;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.repository.EmployeeRep;
import com.wtfcinema.demo.repository.UserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServices {
    @Autowired
    private EmployeeRep employeeRepo;

    public Employee getById(Long id){
        Optional<Employee> result = employeeRepo.findById(id);
        return result.orElse(null);
    }

    public Employee getByEmail(String email){
        Optional<Employee> result = employeeRepo.findByEmailWithTickets(email);
        return result.orElse(null);
    }

    public List<Employee> getAll()
    {
        return employeeRepo.findAll();
    }

    public void registerNewEmployee(Employee employee) {
        if (employeeRepo.findByEmail(employee.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }
        employeeRepo.save(employee);
    }
    public void updateEmployee(Employee employee) {
        employeeRepo.save(employee);
    }
}
