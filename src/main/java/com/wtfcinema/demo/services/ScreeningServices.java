package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.repository.ScreeningRep;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScreeningServices {
    private final ScreeningRep screeningRep;

    public ScreeningServices(ScreeningRep screeningRep) {
        this.screeningRep = screeningRep;
    }

    public void registerNewFunction(Screening newFunction) {
        screeningRep.save(newFunction);
    }

    public Optional<Screening> findById(long id) { return screeningRep.findById(id);}
}
