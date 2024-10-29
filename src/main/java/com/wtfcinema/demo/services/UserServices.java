package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.repository.UserRep;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRep userRepo;

    public User getById(Long id){
        Optional<User> result = userRepo.findById(id);
        return result.orElse(null);
    }

    public User getByEmail(String email){
        Optional<User> result = userRepo.findByEmail(email);
        return result.orElse(null);
    }

    public List<User> getAll()
    {
        return userRepo.findAll();
    }

    public User createUser(String name, String email, LocalDate birthDate, Long phoneNumber, String password) {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
        return userRepo.save(newUser);
    }

    public User createUserWCard(String name, String email, LocalDate birthDate, Long cardNumber, Long phoneNumber, String password)
    {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .cardNumber(cardNumber)
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
        return userRepo.save(newUser);
    }

    public void registerNewUser(User user) {
        // Validar si el usuario ya existe
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }

        // Guardar el usuario en la base de datos
        userRepo.save(user);
    }
}
