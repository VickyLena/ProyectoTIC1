package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.repository.CinemaRep;
import com.wtfcinema.demo.services.*;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
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
    private EmployeeServices employeeService;

    @Autowired
    private CinemaRep cinemaRep;

    @Autowired
    private SnackServices snackServices;

    @Autowired
    private TicketServices ticketServices;

    @Autowired
    private HttpSession session;

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        return "main";
    }

    /////LOGIN////

    @GetMapping("/login")
    public String showLogin(Model model) {
        return "login";
    }

    @PostMapping("/login-request")
    public String useLogin(@RequestParam String email,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        User usuario = userService.getByEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            session.setAttribute("USER", usuario);
            model.addAttribute("user", usuario);
            return "redirect:/movies";
        }

        Employee empleado = employeeService.getByEmail(email);
        if (empleado != null && empleado.getPassword().equals(password)) {
            session.setAttribute("EMPLOYEE", empleado);
            model.addAttribute("employee", empleado);
            return "redirect:/admin/moviesAdmin";
        }

        model.addAttribute("errorMessage", "Email o contraseña incorrectas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/login";
    }

    /////REGISTER/////

    @GetMapping("/register")
    public String showRegister(Model model) {
        return "register";
    }

    @PostMapping("/register-request")
    public String useRegister(@RequestParam String name,
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

            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .birthDate(birthDate)
                    .cardNumber(cardNumber)
                    .phoneNumber(phoneNumber)
                    .password(password)
                    .build();

            userService.registerNewUser(newUser);

            session.setAttribute("USER", newUser);
            model.addAttribute("message", "Usuario registrado exitosamente");
            model.addAttribute("user", newUser);
            return "redirect:/movies";

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "ERROR");
            return "register";
        }
    }

    /////MOVIES//////
    @Transactional
    @GetMapping("/movies")
    public String showMovies(Model model, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
//        if(model.containsAttribute("message")){
//            model.addAttribute("message", movies);
//        }

        if (!model.containsAttribute("movies")) {
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
//            for (Movie movie : movies) {
//                // Inicializar la colección 'genres' explícitamente
//                Hibernate.initialize(movie.getGenres());
//            }
        }
        return "movies";
    }

    @GetMapping("/movie/{movie_id}")
    public String getMovieDetails(@PathVariable Long movie_id, Model model, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("USER");

        Optional<Movie> movieOpt = movieService.findByIdWithScreenings(movie_id);
        if (movieOpt.isPresent()) {
            model.addAttribute("movie", movieOpt.get());
        } else {
            return "redirect:/movies";
        }

        Movie movie = movieOpt.get();
        if(loggedInUser!=null && movie.getAgeRestriction()>Period.between(loggedInUser.getBirthDate(),LocalDate.now()).getYears()){
            redirectAttributes.addFlashAttribute("errorMessage", "El usuario no tiene edad para ver esta pelicula.");
            return "redirect:/movies";
        }

        return "movieScreenings";
    }

    @GetMapping("/movieScreenings")
    public String showMovieScreenings(Model model) {
        return "movieScreenings";
    }

    @GetMapping("/locations")
    public String showLocations(Model model) {
        List<Cinema> cinemas = cinemaRep.findAll();
        model.addAttribute("cinemas", cinemas);
        return "locations";
    }

    @GetMapping("/locationsMenu")
    public String showLocationsMenu(Model model) {
        List<Cinema> cinemas = cinemaRep.findAll();
        model.addAttribute("cinemas", cinemas);
        return "locationsMenu";
    }

    @GetMapping("/snacksMenu")
    public String showSnacksMenu(Model model) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
        return "snacksMenu";
    }

    @GetMapping("/snacks/{ticketId}")
    public String showSnacks(Model model, @PathVariable Long ticketId) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        Ticket ticket= ticketServices.findById(ticketId).get();
        model.addAttribute("ticket", ticket);
        return "snacks";
    }

    @GetMapping("/my-tickets")
    public String showMyTickets(Model model) {
        User loggedInUser = (User) session.getAttribute("USER");
        List<Ticket> userTickets = loggedInUser.getTickets();
        model.addAttribute("userTickets", userTickets);
        return "myTickets";
    }


}
