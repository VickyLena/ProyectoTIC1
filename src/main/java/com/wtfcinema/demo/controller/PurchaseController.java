package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.ScreeningServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
public class PurchaseController {
    @Autowired
    private ScreeningServices screeningServices;

    @Autowired
    private HttpSession session;

    @GetMapping("/seats/{screening_id}")
    public String showSeats(Model model, @PathVariable Long screening_id) {
        Optional<Screening> screening = screeningServices.findById(screening_id);
        List<Ticket> tickets = screening.get().getTakenSeats();
        List<String> takenSeats = new LinkedList<>();
        for (Ticket ticket : tickets) {
            takenSeats.add(ticket.getSeat());
        }
        model.addAttribute("takenSeats", takenSeats);
        model.addAttribute("screening", screening);
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
        return "seats";
    }

    @PostMapping("/seat-selection")
    public String seatSelection(Model model, @RequestParam List<Integer> seats) {
        return "seats";
    }

}
