package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Employee;
import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.EmployeeServices;
import com.wtfcinema.demo.services.MovieServices;
import com.wtfcinema.demo.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("")
public class MainController {
    @Autowired
    private MovieServices movieService;

    @Autowired
    private UserServices userService;

    @Autowired
    private EmployeeServices employeeService;

    // Muestra la página principal en HTML
    @GetMapping
    public String showHomePage() {
        return "main";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    // Muestra la lista de películas en una página HTML
    @GetMapping("/movies")
    public String getAllMovies(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies); // Envía la lista de películas al modelo para la vista
        return "movies"; // Spring buscará un archivo "movies.html" en templates
    }

    // Maneja el registro de usuarios y redirige a una página de confirmación
    @PostMapping("/register-request")
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
            return "redirect:/movies"; // Página de confirmación (registration-confirmation.html en templates)

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/register"; // Redirige al formulario de registro en caso de error
        }
    }


    // Maneja el inicio de sesión de usuarios
    @PostMapping("/login-request")
    public String loginUser(@RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes) {
        //Chequeo que sea User
        User usuario = userService.getByEmail(email);
        if (usuario != null) {
            //chequeo contrasenia
            if (usuario.getPassword().equals(password)) {
                return "redirect:/movies";
            }
            redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: User password does not match!");
            return "redirect:/login";
        }
        // Si no es User chequeo que sea Employee
        Employee empleado = employeeService.getByEmail(email);
        if (empleado != null) {
            //chequeo contrasenia
            if (empleado.getPassword().equals(password)) {
                return "redirect:/movies";
            }
            redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: Employee password does not match!");
            return "redirect:/login";
        }

        // Si no está en ninguno, mostrar mensaje de error
        redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: Email not attached to a User or Employee");
        return "redirect:/login";
    }
}
