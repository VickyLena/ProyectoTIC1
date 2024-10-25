package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.repository.MovieRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Movie addMovie(Movie movie) {
        return movieRep.save(movie);
    }

}

