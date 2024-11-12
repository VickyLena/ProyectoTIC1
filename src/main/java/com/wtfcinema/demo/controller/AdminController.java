package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.services.MovieServices;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.SnackServices;
import com.wtfcinema.demo.services.TheatreServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private HttpSession session;

    @Autowired
    private MovieServices movieServices;

    @Autowired
    private SnackServices snackServices;

    @Autowired
    private TheatreServices theatreServices;

    @Autowired
    private ScreeningServices screeningServices;

    @GetMapping("/moviesAdmin")
    public String showMoviesAdmin(Model model, @ModelAttribute("message") String message) {
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");
        model.addAttribute("user", loggedInUser);

        if (!model.containsAttribute("movies")) {
            List<Movie> movies = movieServices.getAllMovies();
            model.addAttribute("movies", movies);
        }
        model.addAttribute("message", message);
        return "moviesAdmin";
    }

    @GetMapping("/createMovie")
    public String showCreateMovie(Model model) {
        return "createMovie";
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
            if (movieServices.findByTitle(title) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "La película ya está registrada.");
                return "redirect:/admin/createMovie";
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

            movieServices.registerNewMovie(newMovie);

            session.setAttribute("MOVIE", newMovie);
            redirectAttributes.addFlashAttribute("message", "Pelicula registrada exitosamente");
            return "redirect:/admin/moviesAdmin";

        } catch (RuntimeException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "ERROR");
            return "redirect:/admin/createMovie";
        }
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

    @PostMapping("/register-snack")
    public String registerSnack(@RequestParam String name,
                                @RequestParam int price,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        try {
            if (snackServices.findByName(name).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El snack no existe.");
                return "redirect:/admin/createFunction";
            }

            Snack newSnack = Snack.builder()
                    .name(name)
                    .price(price)
                    .build();

            snackServices.registerNewSnack(newSnack);

            session.setAttribute("SNACK", newSnack);
            redirectAttributes.addFlashAttribute("message", "Snack agregado exitosamente");
            return "redirect:/admin/moviesAdmin";

        } catch (RuntimeException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
            return "redirect:/admin/createFunction";
        }
    }

    @GetMapping("/createFunction")
    public String showCreateFunction(Model model) {
        List<Movie> movies = movieServices.getAllMovies();
        model.addAttribute("movies", movies);
        List<Theatre> theatres = theatreServices.getAllTheatres();
        model.addAttribute("theatres", theatres);
        return "createFunction";
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
            Optional<Movie> movie = movieServices.findById(Long.parseLong(movie_id));
            if (movie.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "La película no existe.");
                return "redirect:/admin/createFunction";
            }
            Movie movie1 = movie.get();

            Optional<Theatre> theatre = theatreServices.findById(Long.parseLong(theatre_id));
            if (theatre.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El teatro no existe.");
                return "redirect:/admin/createFunction";
            }
            Theatre theatre1 = theatre.get();

            Screening newFuncion = Screening.builder()
                    .dateTime(dateTime)
                    .language(language)
                    .movie(movie1)
                    .theatre(theatre1)
                    .build();

            screeningServices.registerNewScreening(newFuncion);

            session.setAttribute("SCREENING", newFuncion);
            redirectAttributes.addFlashAttribute("message", "Función registrada exitosamente");
            return "redirect:/admin/moviesAdmin";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
            return "redirect:/admin/createFunction";
        }
    }
}
