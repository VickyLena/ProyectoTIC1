package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.repository.CinemaRep;
import com.wtfcinema.demo.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private TheatreServices theatreServices;

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

    @Autowired
    private SnackServices snackServices;
    @Autowired
    private TicketServices ticketServices;

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
            return "main";

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "ERROR");
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
    public String showMoviesAdmin(Model model, @ModelAttribute("message") String message) {
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");
        model.addAttribute("user", loggedInUser);

        if (!model.containsAttribute("movies")) {
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
        }
        model.addAttribute("message", message);
        return "moviesAdmin";
    }

    @GetMapping("/movieScreenings")
    public String showMovieScreenings(Model model) {
        return "movieScreenings";
    }

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
    public String seatSelection(Model model, @RequestParam List<Integer> seats) {
        return "seats";
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

    @GetMapping("/createMovie")
    public String showCreateMovie(Model model) {
        return "createMovie";
    }

    @GetMapping("/snacksMenu")
    public String showSnacksMenu(Model model) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
        return "snacksMenu";
    }

    @GetMapping("/snacksMenuAdmin")
    public String showSnacksMenuAdmin(Model model) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");
        model.addAttribute("user", loggedInUser);
        return "snacksMenuAdmin";
    }

    @GetMapping("/createSnacks")
    public String showCreateSnacks(Model model) {
        return "createSnacks";
    }

    @GetMapping("/snacks/{ticketId}")
    public String showSnacks(Model model, @PathVariable Long ticketId) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        Ticket ticket= ticketServices.findById(ticketId).get();
        model.addAttribute("ticket", ticket);
        return "snacks";
    }

    @GetMapping("/createFunction")
    public String showCreateFunction(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        List<Theatre> theatres = theatreServices.getAllTheatres();
        model.addAttribute("theatres", theatres);
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
    public String registerMovie(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String director,
                                @RequestParam LocalDate release_date,
                                @RequestParam String duration,
                                @RequestParam List<String> genres,
                                @RequestParam int age_restriction,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        try {
            if (movieService.findByTitle(title) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "La película ya está registrada.");
                return "redirect:/createMovie";
            }
            Date date = Date.from(release_date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Movie newMovie = Movie.builder()
                    .title(title)
                    .description(description)
                    .director(director)
                    .releaseDate(date)
                    .duration(duration)
                    .genres(genres)
                    .ageRestriction(age_restriction)
                    .build();

            movieService.registerNewMovie(newMovie);

            session.setAttribute("MOVIE", newMovie);
            redirectAttributes.addFlashAttribute("message", "Pelicula registrada exitosamente");
            return "redirect:/moviesAdmin";

        } catch (RuntimeException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "ERROR");
            return "redirect:/createMovie";
        }
    }

    @PostMapping("/register-function")
    public String registerFunction(@RequestParam String date_time,
                                   @RequestParam String language,
                                   @RequestParam String movie_id,
                                   @RequestParam String theatre_id,
                                   Model model,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date_time);
            Optional<Movie> movie = movieService.findById(Long.parseLong(movie_id));
            if (movie.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "La película no existe.");
                return "redirect:/createFunction";
            }
            Movie movie1 = movie.get();

            Optional<Theatre> theatre = theatreServices.findById(Long.parseLong(theatre_id));
            if (theatre.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El teatro no existe.");
                return "redirect:/createFunction";
            }
            Theatre theatre1 = theatre.get();

            Screening newFuncion = Screening.builder()
                    .dateTime(dateTime)
                    .language(language)
                    .movie(movie1)
                    .theatre(theatre1)
                    .build();

            screeningServices.registerNewFunction(newFuncion);

            session.setAttribute("SCREENING", newFuncion);
            redirectAttributes.addFlashAttribute("message", "Función registrada exitosamente");
            return "redirect:/moviesAdmin";

        } catch (RuntimeException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
            return "redirect:/createFunction";
        }
    }

    @PostMapping("/register-snack")
    public String registerSnack(@RequestParam String name,
                                @RequestParam int price,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        try {
            if (snackServices.findByName(name).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El snack no existe.");
                return "redirect:/createFunction";
            }

            Snack newSnack = Snack.builder()
                    .name(name)
                    .price(price)
                    .build();

            snackServices.registerNewSnack(newSnack);

            session.setAttribute("SNACK", newSnack);
            redirectAttributes.addFlashAttribute("message", "Snack agregado exitosamente");
            return "redirect:/moviesAdmin";

        } catch (RuntimeException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
            return "redirect:/createFunction";
        }
    }
}
