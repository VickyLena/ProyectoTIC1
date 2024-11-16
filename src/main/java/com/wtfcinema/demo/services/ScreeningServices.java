package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Snack;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.repository.ScreeningRep;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScreeningServices {

    @Autowired
    private ScreeningRep screeningRep;

    public ScreeningServices(ScreeningRep screeningRep) {
        this.screeningRep = screeningRep;
    }

    public void registerNewScreening(Screening newFunction) {
        screeningRep.save(newFunction);
    }

    @Transactional
    public Optional<Screening> findById(long id) { return screeningRep.findById(id);}

    @Transactional
    public void deleteScreeningById(Long screening) {
        Optional<Screening> screeningOptional = screeningRep.findById(screening);
        if (screeningOptional.isEmpty()) {
            throw new IllegalArgumentException("Screening con id '" + screening + "' no encontrado en la base de datos.");
        }
        try {
            screeningRep.deleteById(screening);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la funcion con id '" + screening + "'. Error: " + e.getMessage());
        }
    }

    @Transactional
    public Screening getScreeningWithTakenSeats(Long screeningId) {
        Optional<Screening> screening = screeningRep.findByIdWithTickets(screeningId);
        if(!screening.isEmpty()){
            screening.get().getTakenSeats(); // Inicializa la colección
        }
        return screening.orElse(null);
    }
}
