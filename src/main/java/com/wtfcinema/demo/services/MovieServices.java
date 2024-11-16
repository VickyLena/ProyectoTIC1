package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.repository.MovieRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServices {

    @Autowired
    private MovieRep movieRep;

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

    public void registerNewMovie(Movie movie) {
        if (movieRep.findByTitle(movie.getTitle()).isPresent()) {
            throw new RuntimeException("El título de la película ya está en uso.");
        }
        movieRep.save(movie);
    }
}

