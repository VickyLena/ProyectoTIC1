package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.repository.CinemaRep;
import com.wtfcinema.demo.repository.UserRep;
import com.wtfcinema.demo.services.*;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.*;

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
    private UserRep userRep;

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
            User usuario2 = userService.getByEmail(email);
            session.setAttribute("USER", usuario);
            model.addAttribute("user", usuario);
            return "redirect:/movies";
        }

        Employee empleado = employeeService.getByEmail(email);
        if (empleado != null && empleado.getPassword().equals(password)) {
            session.setAttribute("EMPLOYEE", empleado);
            model.addAttribute("employee", empleado);
            return "redirect:/admin/movies";
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
    public String gotoMovieScreenings(@PathVariable Long movie_id, Model model, RedirectAttributes redirectAttributes) {
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
        model.addAttribute("user", ticketServices.findById(ticketId).get().getUser());
        model.addAttribute("snacks", snackList);
        model.addAttribute("ticket", ticketId);
        return "snacks";
    }

    @Transactional
    @GetMapping("/my-tickets")
    public String showMyTickets(Model model) {
        User loggedInUser = (User) session.getAttribute("USER");
        User userWithTickets = userRep.findByIdWithTickets(loggedInUser.getId()).orElseThrow();
        model.addAttribute("userTickets", userWithTickets.getTickets());

        List<Ticket> tickets = userWithTickets.getTickets();
        tickets.forEach(ticket -> Hibernate.initialize(ticket.getSnacks()));

        return "myTickets";
    }

    @GetMapping("/delete-user")
    public String deleteUser(Model model, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("USER");
        if (loggedInUser != null) {
            userService.deleteUser(loggedInUser.getId());
            return "redirect:/";
        }
        return "redirect:/movies";
    }

    @PostMapping("/delete-ticket/{ticketId}")
    public String deleteTicket(Model model, @PathVariable Long ticketId, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("USER");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: No estas iniciado sesion.");
            return "redirect:/my-tickets";
        }
        Optional<Ticket> ticketOptional = ticketServices.findById(ticketId);
        if (ticketOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: Ticket no encontrado.");
            return "redirect:/my-tickets";
        }

        if (ticketOptional.get().getSnacks()==null){
            ticketServices.deleteById(ticketId);
            redirectAttributes.addFlashAttribute("errorMessage", "Ticket eliminado exitosamente.");
            return "redirect:/my-tickets";
        }

        Ticket ticket = ticketOptional.get();
        List<Ticket> userTicketsForScreening = loggedInUser.getTickets().stream()
                .filter(t -> t.getScreening().equals(ticket.getScreening()))
                .toList();

        if (userTicketsForScreening.size() > 1){
            int i=0;
            while (userTicketsForScreening.get(i).getId()==ticket.getId()){
                i++;
            }
            for (Snack snack : userTicketsForScreening.get(i).getSnacks()) {
                userTicketsForScreening.get(i).getSnacks().add(snack);
            }
            ticketServices.deleteById(ticketId);
            redirectAttributes.addFlashAttribute("errorMessage", "Ticket con sus snacks eliminado exitosamente.");
            return "redirect:/my-tickets";
        }

        ticketServices.deleteById(ticketId);
        redirectAttributes.addFlashAttribute("errorMessage", "Ticket con sus snacks eliminado exitosamente.");
        return "redirect:/my-tickets";
    }

    @PostMapping("/add-snack/{ticketId}")
    public ResponseEntity<String> addSnack(Model model, @PathVariable Long ticketId, @RequestBody List<String> snackList) {
        Optional<Ticket> ticketOptional = ticketServices.findById(ticketId);
        if (ticketOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ERROR: Ticket no encontrado.");
        }
        List<Snack> snackObjectList = new ArrayList<>();
        for (String snack : snackList) {
            Optional<Snack> snackObj = snackServices.findByName(snack);
            if (snackObj.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("ERROR: Snack no encontrado.");
            }
            snackObjectList.add(snackObj.get());
        }
        snackObjectList.addAll(ticketOptional.get().getSnacks());
        ticketOptional.get().setSnacks(snackObjectList);
        ticketServices.editTicket(ticketOptional.get());

        return ResponseEntity.ok("redirect:/my-tickets");
    }

}
