package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.MovieServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/wtf-cinema")
public class MainController {
    @Autowired
    private MovieServices movieServices;

    // Muestra la página principal en HTML
    @GetMapping
    public String showHomePage() {
        return "main";
    }

    // Muestra la lista de películas en una página HTML
    @GetMapping("/movies")
    public String getAllMovies(Model model) {
        List<Movie> movies = movieServices.getAllMovies();
        model.addAttribute("movies", movies); // Envía la lista de películas al modelo para la vista
        return "movies"; // Spring buscará un archivo "movies.html" en templates
    }

    // Maneja el registro de usuarios y redirige a una página de confirmación
//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute User user, Model model) {
//        // Lógica de registro de usuario
//        model.addAttribute("message", "User registered successfully");
//        return "registration-confirmation"; // Página de confirmación (registration-confirmation.html en templates)
//    }

    // Maneja el registro de usuarios y redirige a una página de confirmación
    @PostMapping("/login")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Lógica de registro de usuario
        model.addAttribute("message", "User logged in successfully");
        return "registration-confirmation"; // Página de confirmación (registration-confirmation.html en templates)
    }
}
