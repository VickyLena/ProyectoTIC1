package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.Theatre;
import com.wtfcinema.demo.repository.MovieRep;
import com.wtfcinema.demo.repository.TheatreRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TheatreServices {

    @Autowired
    private TheatreRep theatreRep;

    public Optional<Theatre> findById(long id) {return theatreRep.findById(id);}

    public List<Theatre> getAllTheatres() {
        return theatreRep.findAll();
    }
}
