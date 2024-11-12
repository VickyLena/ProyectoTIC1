package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.TicketServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
public class PurchaseController {
    @Autowired
    private ScreeningServices screeningServices;

    @Autowired
    private HttpSession session;

    @Autowired
    private TicketServices ticketServices;

    @GetMapping("/seats/{screening_id}")
    public String showSeats(Model model, @PathVariable Long screening_id) {
        Optional<Screening> screening = screeningServices.findById(screening_id);
        List<Ticket> tickets = screening.get().getTakenSeats();
        List<String> takenSeats = new LinkedList<>();
        for (Ticket ticket : tickets) {
            takenSeats.add(ticket.getSeat());
        }
        model.addAttribute("takenSeats", takenSeats);
        model.addAttribute("screeningId", screening_id);
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
        return "seats";
    }

    @GetMapping("/payment-method/{screening_id}/{seats}")
    public String selectPaymentMethod(Model model, @PathVariable Long screening_id, @PathVariable String seats){
        System.out.println("screening_id: " + screening_id);
        model.addAttribute("screeningId", screening_id);
        model.addAttribute("seats", seats);
        return "paymentMethod";}

    @GetMapping("/new-card/{screening_id}/{seats}")
    public String newCard(Model model, @PathVariable Long screening_id, @PathVariable String seats){
        System.out.println("screening_id: " + screening_id);
        model.addAttribute("screeningId", screening_id);
        model.addAttribute("seats", seats);
        return "newCard";}

    @PostMapping("/addCard")
    public String addCard(Model model, @RequestParam Long cardNumber, @RequestParam Boolean permanent, RedirectAttributes redirectAttributes){
        int length = String.valueOf(cardNumber).length();
        if (length!= 16){
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: El numero de tarjeta debe tener largo 16");
            return "redirect:/new-card/{screening_id}/{seats}";
        }

        if (permanent){
            User loggedInUser = (User) session.getAttribute("USER");
            loggedInUser.setCardNumber(cardNumber);
        }

        return "redirect:/payed/{screening_id}/{seats}";
    }

    @PostMapping("/payed/{screening_id}/{seats}")
    public String confirmPayment(Model model, @PathVariable Long screening_id, @PathVariable String seats){
        User loggedInUser = (User) session.getAttribute("USER");
        Optional<Screening> screening = screeningServices.findById(screening_id);
        List<String> selectedSeats = List.of(seats.split(","));
        for (String number : selectedSeats){
            Ticket newTicket = Ticket.builder()
                    .seat(number)
                    .user(loggedInUser)
                    .screening(screening.get())
                    .build();
            ticketServices.registerNewTicket(newTicket);
        }
        return "redirect:/my-tickets";
    }
}
