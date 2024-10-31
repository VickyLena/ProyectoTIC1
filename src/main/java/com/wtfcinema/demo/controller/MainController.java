package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Employee;
import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.EmployeeServices;
import com.wtfcinema.demo.services.MovieServices;
import com.wtfcinema.demo.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    private MovieServices movieService;

    @Autowired
    private UserServices userService;

    @Autowired
    private EmployeeServices employeeService;

    @Autowired
    private HttpSession session;

    // Muestra la página principal en HTML
    @GetMapping
    public String showHomePage() {
        return "main";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "redirect:/register.html";
    }

    @GetMapping("/movies")
    public String showMovies(Model model) {
        // Retrieve the current user from the session
        User loggedInUser = (User) session.getAttribute("USER");

        // Pass the user information to the model
        model.addAttribute("user", loggedInUser);

        return "movies";
    }


//    // Muestra la lista de películas en una página HTML
//    @GetMapping("/movies")
//    public String getAllMovies(Model model) {
//        List<Movie> movies = movieService.getAllMovies();
//        model.addAttribute("movies", movies);
//        return "redirect:/movies";
//    }

    // Maneja el registro de usuarios y redirige a una página de confirmación
    @PostMapping("/register-request")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam(required = false) Long cardNumber,
                               @RequestParam LocalDate birthDate,
                               @RequestParam Long phoneNumber,
                               @RequestParam String password,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            if (userService.getByEmail(email) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "El correo electrónico ya está registrado");
                return "redirect:/register";
            }

            // Crear un nuevo objeto User utilizando los parámetros recibidos
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .birthDate(birthDate)
                    .cardNumber(cardNumber)
                    .phoneNumber(phoneNumber)
                    .password(password)
                    .build();

            userService.registerNewUser(newUser);

            // Iniciar sesión automáticamente para el nuevo usuario
            session.setAttribute("USER", newUser);
            model.addAttribute("message", "Usuario registrado exitosamente");
            return "main";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    // Maneja el inicio de sesión de usuarios
    @PostMapping("/login-request")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes) {
        User usuario = userService.getByEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            session.setAttribute("USER", usuario);
            return "redirect:/movies";
        }

        Employee empleado = employeeService.getByEmail(email);
        if (empleado != null && empleado.getPassword().equals(password)) {
            session.setAttribute("EMPLOYEE", empleado);
            return "redirect:/movies";
        }

        redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: Email or password does not match!");
        return "redirect:/login";
    }

    // Maneja el cierre de sesión
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/login";
    }
}
