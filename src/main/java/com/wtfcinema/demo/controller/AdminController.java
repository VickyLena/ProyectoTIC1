package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.services.MovieServices;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.SnackServices;
import com.wtfcinema.demo.services.TheatreServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

            // Guarda el archivo en la carpeta static/movies
            if (!file.isEmpty()) {
                String fileExtension = ".jpg";
                String directoryPath = "src/main/resources/static/images/";
                String filePath = directoryPath + newMovie.getId() + fileExtension;

                // Crear el directorio si no existe
                Path directory = Paths.get(directoryPath);
                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }

                // Obtener la ruta completa del archivo
                Path path = Paths.get(filePath);

                // Guardar el archivo (sobreescribirá si ya existe uno con el mismo nombre)
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
}
