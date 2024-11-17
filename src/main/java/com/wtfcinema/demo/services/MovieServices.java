package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Employee;
import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.repository.MovieRep;
import com.wtfcinema.demo.repository.ScreeningRep;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServices {

    @Autowired
    private MovieRep movieRep;
    @Autowired
    private ScreeningRep screeningRep;

    @Transactional
    public List<Movie> getAllMovies() {
        return movieRep.findAllWithGenres();
    }

    public List<Movie> findByAgeRestriction(int ageRestriction) {
        return movieRep.findByAgeRestriction(ageRestriction);
    }

    @Transactional
    public Optional<Movie> findByIdWithScreenings(Long movieId) {
        return movieRep.findByIdWithScreenings(movieId);
    }

    public Movie addMovie(String title, List<String> genres, Date releaseDate) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenres(genres);
        movie.setReleaseDate(releaseDate);
        return movieRep.save(movie);
    }
    public Movie findByTitle(String title) {
        Optional<Movie> movie = movieRep.findByTitle(title);
        return movie.orElse(null);
    }
    public Movie findByIdWithGenres(Long movieId) {
        Optional<Movie> movie = movieRep.findByIdWithGenres(movieId);
        return movie.orElse(null);
    }

    public Movie findById(Long movieId) {
        Optional<Movie> movie = movieRep.findById(movieId);
        return movie.orElse(null);
    }

    public void registerNewMovie(Movie movie) {
        if (movieRep.findByTitle(movie.getTitle()).isPresent()) {
            throw new RuntimeException("El título de la película ya está en uso.");
        }
        movieRep.save(movie);
    }

    @Transactional
    public void deleteMovieById(Long movieId) {
        Optional<Movie> movieOptional = movieRep.findById(movieId);
        if (movieOptional.isEmpty()) {
            throw new IllegalArgumentException("Pelicula con id '" + movieId + "' no encontrado en la base de datos.");
        }
        try {
            for (Screening screening : movieOptional.get().getScreenings()) {
                if (screening.getMovie().getId() == movieId) {
                    screeningRep.deleteById(screening.getId());
                }
            }
            movieRep.deleteById(movieId);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la pelicula con id '" + movieId + "'. Error: " + e.getMessage());
        }
    }

    public void update(Movie updatedMovie) {
        Movie existingMovie = movieRep.findById(updatedMovie.getId())
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con id: " + updatedMovie.getId()));

        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setDescription(updatedMovie.getDescription());
        existingMovie.setDirector(updatedMovie.getDirector());
        existingMovie.setDuration(updatedMovie.getDuration());
        existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
        existingMovie.setGenres(updatedMovie.getGenres());
        existingMovie.setAgeRestriction(updatedMovie.getAgeRestriction());

        movieRep.save(existingMovie);
    }

    public List<Movie> findByGenre(String genre) {
        return movieRep.findByGenre(genre);
    }
}

