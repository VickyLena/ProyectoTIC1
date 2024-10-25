package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.repository.UserRep;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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

    public List<User> getAll()
    {
        return userRepo.findAll();
    }

    public User createUser(String name, String email, Date birthDate, Long phoneNumber, String password) {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
        return userRepo.save(newUser);
    }

    public User createUserWCard(String name, String email, Date birthDate, Long cardNumber, Long phoneNumber, String password)
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
}
