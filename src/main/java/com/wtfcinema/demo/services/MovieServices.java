package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.repository.MovieRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServices {

    @Autowired
    private MovieRep movieRep;

    public List<Movie> getAllMovies() {
        return movieRep.findAll();
    }

    public List<Movie> findByAgeRestriction(int ageRestriction) {
        return movieRep.findByAgeRestriction(ageRestriction);
    }

    public Optional<Movie> findById(long id) {return movieRep.findById(id);}

    public Movie addMovie(String title, List<String> genres, Date releaseDate) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenres(genres);
        movie.setReleaseDate(releaseDate);
        return movieRep.save(movie);
    }
}

