package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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

    @Autowired
    private TicketServices ticketServices;

    @Autowired
    private UserServices userService;
    @Autowired
    private EmployeeServices employeeServices;

    /////MOVIES/////

    @GetMapping("/moviesAdmin")
    public String showMoviesAdmin(Model model, @ModelAttribute("message") String message) {
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");
        model.addAttribute("employee", loggedInUser);

        if (!model.containsAttribute("movies")) {
            List<Movie> movies = movieServices.getAllMovies();
            model.addAttribute("movies", movies);
        }
        model.addAttribute("message", message);
        return "moviesAdmin";
    }

    @GetMapping("/movie/{movie_id}")
    public String getMovieDetailsAdmin(@PathVariable Long movie_id, Model model, RedirectAttributes redirectAttributes) {
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");

        Optional<Movie> movieOpt = movieServices.findByIdWithScreenings(movie_id);
        if (movieOpt.isPresent()) {
            model.addAttribute("movie", movieOpt.get());
        } else {
            return "redirect:/admin/moviesAdmin";
        }
        return "movieScreeningsAdmin";
    }

    @GetMapping("/movieScreenings")
    public String showMovieScreenings(Model model) {
        return "movieScreeningsAdmin";
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
                                @RequestParam int duration,
                                @RequestParam List<String> genres,
                                @RequestParam int age_restriction,
                                @RequestParam("file") MultipartFile file,
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

            if (!file.isEmpty()) {
                String fileExtension = ".jpg";
                String directoryPath = "src/main/resources/static/images/";
                String filePath = directoryPath + newMovie.getId() + fileExtension;

                Path directory = Paths.get(directoryPath);
                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }

                Path path = Paths.get(filePath);

                Files.write(path, file.getBytes());
            }

            session.setAttribute("MOVIE", newMovie);
            redirectAttributes.addFlashAttribute("message", "Película registrada exitosamente");
            return "redirect:/admin/moviesAdmin";

        } catch (RuntimeException | IOException e) {
            model.addAttribute("errorMessage", "Error al registrar la película o al guardar la imagen.");
            return "redirect:/admin/createMovie";
        }
    }

    @PostMapping("/deleteMovie/{movieId}")
    public String deleteMovie(@PathVariable Long movieId, RedirectAttributes redirectAttributes) {
        try {
            Optional<Movie> movie= movieServices.findByIdWithScreenings(movieId);

            if (movie.isPresent()) {
                String photoName = movie.get().getId() + ".jpg";

                Path photoPath = Paths.get("src/main/resources/static/images/", photoName);

                // Intentar eliminar el archivo si existe
                try {
                    if (Files.exists(photoPath)) {
                        Files.delete(photoPath);
                    }
                } catch (IOException ignored) {} //si no se encuentra la foto sigue, para ver cual era el error especifico abajo
            }

            movieServices.deleteMovieById(movieId);
            redirectAttributes.addFlashAttribute("errorMessage", "Pelicula eliminada exitosamente.");
            return "redirect:/admin/moviesAdmin";


        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Pelicula no encontrada. " + e);
            return "redirect:/admin/moviesAdmin";
        } catch (RuntimeException r) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la Pelicula. " + r);
            return "redirect:/admin/moviesAdmin";
        }
    }

    /////SNACKS/////

    @GetMapping("/snacksMenuAdmin")
    public String showSnacksMenuAdmin(Model model) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");
        model.addAttribute("employee", loggedInUser);
        return "snacksMenuAdmin";
    }

    @GetMapping("/createSnacks")
    public String showCreateSnacks(Model model) {
        return "createSnacks";
    }

    @PostMapping("/register-snack")
    public String registerSnack(@RequestParam String name,
                                @RequestParam int price,
                                @RequestParam("file") MultipartFile file,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        try {
            if (snackServices.findByName(name).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El snack ya existe.");
                return "redirect:/admin/createSnacks";
            }

            Snack newSnack = Snack.builder()
                    .name(name)
                    .price(price)
                    .build();

            snackServices.registerNewSnack(newSnack);

            if (!file.isEmpty()) {
                String fileExtension = ".jpg";
                String directoryPath = "src/main/resources/static/images/snacks/";
                String filePath = directoryPath + newSnack.getName() + fileExtension;

                Path directory = Paths.get(directoryPath);
                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }

                Path path = Paths.get(filePath);

                Files.write(path, file.getBytes());
            }
            session.setAttribute("SNACK", newSnack);
            redirectAttributes.addFlashAttribute("message", "Snack agregado exitosamente");
            return "redirect:/admin/snacksMenuAdmin";

        } catch (RuntimeException | IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
            return "redirect:/admin/createSnacks";
        }
    }

    @PostMapping("/deleteSnack/{snackId}")
    public String deleteSnack(@PathVariable Long snackId, RedirectAttributes redirectAttributes) {
        try {
            Optional<Snack> snack= snackServices.findById(snackId);

            if (snack.isPresent()) {
                String photoName = snack.get().getName() + ".jpg";

                Path photoPath = Paths.get("src/main/resources/static/images/snacks/", photoName);

                // Intentar eliminar el archivo si existe
                try {
                    if (Files.exists(photoPath)) {
                        Files.delete(photoPath);
                    }
                } catch (IOException ignored) {} //si no se encuentra la foto sigue, para ver cual era el error especifico abajo
            }

            snackServices.deleteSnackById(snackId);
            redirectAttributes.addFlashAttribute("errorMessage", "Snack eliminado exitosamente.");
            return "redirect:/admin/snacksMenuAdmin";


        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Snack no encontrado. " + e);
            return "redirect:/admin/snacksMenuAdmin";
        } catch (RuntimeException r) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el snack. " + r);
            return "redirect:/admin/snacksMenuAdmin";
        }
    }

    ////////////SCREENINGS///////////

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
            Optional<Movie> movie = movieServices.findByIdWithScreenings(Long.parseLong(movie_id));
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

    @PostMapping("/deleteScreening/{movieId}/{screeningId}")
    public String deleteScreening(@PathVariable Long screeningId, @PathVariable Long movieId, RedirectAttributes redirectAttributes) {
        try {
            screeningServices.deleteScreeningById(screeningId);
            redirectAttributes.addFlashAttribute("errorMessage", "Funcion eliminada exitosamente.");
            return "redirect:/admin/movie/"+ movieId;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Funcion no encontrada. " + e);
            return "redirect:/admin/movie/"+ movieId;
        } catch (RuntimeException r) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la funcion. " + r);
            return "redirect:/admin/movie/"+ movieId;
        }
    }

    @GetMapping("/edit-profile-admin")
    public String showEditProfile(Model model) {
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("employee", loggedInUser);
        return "editProfileAdmin";
    }

    @PostMapping("/edit-profile-request-admin")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) Long cardNumber,
                                @RequestParam LocalDate birthDate,
                                @RequestParam String address,
                                @RequestParam Long phoneNumber,
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes) {
        Employee loggedInUser = (Employee) session.getAttribute("EMPLOYEE");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        loggedInUser.setName(name);
        loggedInUser.setEmail(email);
        loggedInUser.setBirthDate(birthDate);
        loggedInUser.setAddress(address);
        loggedInUser.setPhoneNumber(phoneNumber);
        loggedInUser.setPassword(password);  // Puedes manejar la validación de contraseñas aquí si es necesario.

        employeeServices.updateEmployee(loggedInUser);
        redirectAttributes.addFlashAttribute("message", "Perfil actualizado correctamente.");
        return "redirect:/admin/moviesAdmin";
    }

    @GetMapping("/edit-movie/{movieId}")
    public String editMovie(@PathVariable("movieId") Long movieId, Model model) {
        Movie movie = movieServices.findByIdWithGenres(movieId);
        model.addAttribute("movie", movie);
        return "redirect:/editMovies";
    }



}
