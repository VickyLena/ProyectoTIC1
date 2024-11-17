package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.TicketServices;
import com.wtfcinema.demo.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PurchaseController {
    @Autowired
    private ScreeningServices screeningServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private HttpSession session;

    @Autowired
    private TicketServices ticketServices;

    @Transactional
    @GetMapping("/seats/{screening_id}")
    public String showSeats(Model model, @PathVariable Long screening_id) {
        Screening screening = screeningServices.getScreeningWithTakenSeats(screening_id);
        List<Ticket> tickets = screening.getTakenSeats();
        List<String> takenSeats = new ArrayList<>();
        for (Ticket ticket : tickets) {
            takenSeats.add(ticket.getSeat());
        }
        System.out.println(takenSeats);
        model.addAttribute("takenSeats", takenSeats);
        model.addAttribute("screeningId", screening_id);
        User loggedInUser = (User) session.getAttribute("USER");
        model.addAttribute("user", loggedInUser);
        return "seats";
    }

    @Transactional
    @GetMapping("/payment-method/{screening_id}/{seats}")
    public String selectPaymentMethod(Model model, @PathVariable Long screening_id, @PathVariable String seats){
        model.addAttribute("screeningId", screening_id);
        model.addAttribute("seats", seats);
        User loggedInUser = (User) session.getAttribute("USER");

        if (loggedInUser.getCardNumber() != null) {
            model.addAttribute("card", loggedInUser.getCardNumber());
        }

        return "paymentMethod";
    }

    @Transactional
    @GetMapping("/new-card/{screening_id}/{seats}")
    public String newCard(Model model, @PathVariable Long screening_id, @PathVariable String seats){
        System.out.println("screening_id: " + screening_id);
        model.addAttribute("screeningId", screening_id);
        model.addAttribute("seats", seats);
        return "newCard";}

    @Transactional
    @PostMapping("/addCard")
    public String addCard(Model model, @RequestParam Long cardNumber, @RequestParam(required = false) Boolean permanent,
                          @RequestParam Long screening_id, @RequestParam String seats, RedirectAttributes redirectAttributes){

        // Verificar que los parámetros están llegando correctamente
        System.out.println("Card Number: " + cardNumber);
        System.out.println("Permanent: " + permanent);
        System.out.println("Screening ID: " + screening_id);
        System.out.println("Seats: " + seats);
        int length = String.valueOf(cardNumber).length();
        if (length!= 16){
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: El numero de tarjeta debe tener largo 16");
            return "redirect:/new-card/" + screening_id + "/" + seats;
        }

        if (permanent == null) {
            permanent = false;
        }

        if (permanent){
            User loggedInUser = (User) session.getAttribute("USER");
//            loggedInUser.setCardNumber(cardNumber);
            System.out.println("llegaaaaaaaaa");
            System.out.println(loggedInUser.getCardNumber());
            userServices.saveUserNewCard(loggedInUser,cardNumber);
        }
        return "redirect:/payed/" + screening_id + "/" + seats;
    }

    @Transactional
    @GetMapping("/payed/{screening_id}/{seats}")
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
