package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.MovieServices;
import com.wtfcinema.demo.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class MainController {
    @Autowired
    private MovieServices movieServices;

    @Autowired
    private UserServices userService;

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
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam(required = false) Long cardNumber,
                               @RequestParam LocalDate birthDate,
                               @RequestParam Long phoneNumber,
                               @RequestParam String password,
                               Model model) {
        try {
            // Crear un nuevo objeto User utilizando los parámetros recibidos
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .birthDate(birthDate)
                    .cardNumber(cardNumber)
                    .phoneNumber(phoneNumber)
                    .password(password) // Asegúrate de que la contraseña sea encriptada en el servicio
                    .build();

            // Llama al servicio para registrar el usuario
            userService.registerNewUser(newUser); // Cambia a createUser si usas ese método

            model.addAttribute("message", "Usuario registrado exitosamente");
            return "main"; // Página de confirmación (registration-confirmation.html en templates)

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "main"; // Redirige al formulario de registro en caso de error
        }
    }


    // Maneja el inicio de sesión de usuarios
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        // Lógica de inicio de sesión
        model.addAttribute("message", "Usuario iniciado sesión exitosamente");
        return "login-confirmation"; // Página de confirmación de inicio de sesión
    }
}
