package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.repository.CinemaRep;
import com.wtfcinema.demo.services.EmployeeServices;
import com.wtfcinema.demo.services.MovieServices;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    private MovieServices movieService;

    @Autowired
    private UserServices userService;

    @Autowired
    private ScreeningServices screeningServices;

    @Autowired
    private EmployeeServices employeeService;

    @Autowired
    private HttpSession session;
    @Autowired
    private CinemaRep cinemaRep;

    // Muestra la página principal en HTML
    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        return "main";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        return "register";
    }


    @GetMapping("/movies")
    public String showMovies(Model model) {
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);

        if (!model.containsAttribute("movies")) {
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
        }
        return "movies";
    }


    // Maneja el registro de usuarios y redirige a una página de confirmación
    @PostMapping("/register-request")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam(required = false) Long cardNumber,
                               @RequestParam LocalDate birthDate,
                               @RequestParam Long phoneNumber,
                               @RequestParam String password,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        try {
            if (userService.getByEmail(email) != null) {
                model.addAttribute("errorMessage", "El correo electrónico ya está registrado");
                return "register";
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
            model.addAttribute("errorMessage", "ERRORRRR");
            return "register";
        }
    }

    // Maneja el inicio de sesión de usuarios
    @PostMapping("/login-request")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        User usuario = userService.getByEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            session.setAttribute("USER", usuario);
            return "redirect:/movies"; //te lleva a movies pero la de main controller, no la de html
        }

        Employee empleado = employeeService.getByEmail(email);
        if (empleado != null && empleado.getPassword().equals(password)) {
            session.setAttribute("EMPLOYEE", empleado);
            return "redirect:/moviesAdmin";
        }

        model.addAttribute("errorMessage", "Email o contraseña incorrectas");
        return "login";
    }

    // Maneja el cierre de sesión
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "login";
    }

    @GetMapping("/movie/{movie_id}")
    public String getMovieDetails(@PathVariable Long movie_id, Model model) {
        Optional<Movie> movieOpt = movieService.findById(movie_id);
        if (movieOpt.isPresent()) {
            model.addAttribute("movie", movieOpt.get());
        } else {
            // Manejo del caso donde la película no existe (podrías redirigir a una página de error)
            return "redirect:/movies"; // Redirige a la lista de películas si no se encuentra la película
        }
        return "movieScreenings";
    }

    @GetMapping("/moviesAdmin")
    public String showMoviesAdmin(Model model){
        return "moviesAdmin";
    }

    @GetMapping("/movieScreenings")
    public String showMovieScreenings(Model model) {return "movieScreenings";}

    @GetMapping("/seats/{screening_id}")
    public String showSeats(Model model, @PathVariable Long screening_id) {
        Optional<Screening> screening = screeningServices.findById(screening_id);
        List<Ticket> tickets = screening.get().getTakenSeats();
        List<String> takenSeats = new LinkedList<>();
        for (Ticket ticket : tickets) {
            takenSeats.add(ticket.getSeat());
        }
        model.addAttribute("takenSeats", takenSeats);
        model.addAttribute("screening", screening);
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
        return "seats";
    }

    @PostMapping("/seat-selection")
    public String seatSelection(Model model,@RequestParam List<Integer> seats) {
        return "seats";
    }

    @GetMapping("/locations")
    public String showLocations(Model model) {
        List<Cinema> cinemas= cinemaRep.findAll();
        model.addAttribute("cinemas", cinemas);
        return "locations";
    }

    @GetMapping("/locationsMenu")
    public String showLocationsMenu(Model model) {
        List<Cinema> cinemas= cinemaRep.findAll();
        model.addAttribute("cinemas", cinemas);
        return "locationsMenu";
    }

    @GetMapping("/createMovie")
    public String showCreateMovie(Model model) {
        return "createMovie";
    }

    @GetMapping("/createFunction")
    public String showCreateFunction(Model model) {
        return "createFunction";
    }

    @GetMapping("/my-tickets")
    public String showMyTickets(Model model) {
        User loggedInUser = (User) session.getAttribute("USER");
        List<Ticket> userTickets = loggedInUser.getTickets();
        model.addAttribute("userTickets", userTickets);
        return "myTickets";
    }

    @PostMapping("/register-movie")
    public String registerMovie(Movie movie) {
        return "redirect:/createFunction";
    }

}