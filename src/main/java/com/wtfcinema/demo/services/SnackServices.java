package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Movie;
import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Snack;
import com.wtfcinema.demo.repository.ScreeningRep;
import com.wtfcinema.demo.repository.SnackRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SnackServices {
    @Autowired
    private SnackRep snackRep;

    public Optional<Snack> findByName(String name) { return snackRep.findByName(name);}

    public void registerNewSnack(Snack newSnack) {
        snackRep.save(newSnack);
    }

    public List<Snack> getAllSnacks() {
        return snackRep.findAll();
    }

    public void deleteSnackById(String snack) {
        Optional<Snack> snackOptional = snackRep.findById(snack);
        if (snackOptional.isEmpty()) {
            throw new IllegalArgumentException("Snack con nombre '" + snack + "' no encontrado en la base de datos.");
        }
        try {
            snackRep.delete(snackOptional.get());
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el snack con nombre '" + snack + "'. Error: " + e.getMessage());
        }
    }

}
