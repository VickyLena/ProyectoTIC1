package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.Screening;
import com.wtfcinema.demo.entities.Snack;
import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.SnackServices;
import com.wtfcinema.demo.services.TicketServices;
import com.wtfcinema.demo.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private SnackServices snackServices;

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

    ////PAYMENT////

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

    /////SNACKS////

    @PostMapping("/add-snack/{ticketId}")
    public ResponseEntity<String> addSnack(Model model, @PathVariable Long ticketId, @RequestBody List<String> snackList) {
        Optional<Ticket> ticketOptional = ticketServices.findById(ticketId);
        if (ticketOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ERROR: Ticket no encontrado.");
        }
        List<Snack> snackObjectList = new ArrayList<>();
        for (String snack : snackList) {
            Optional<Snack> snackObj = snackServices.findByName(snack);
            if (snackObj.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("ERROR: Snack no encontrado.");
            }
            snackObjectList.add(snackObj.get());
        }
        snackObjectList.addAll(ticketOptional.get().getSnacks());
        ticketOptional.get().setSnacks(snackObjectList);
        ticketServices.editTicket(ticketOptional.get());

        return ResponseEntity.ok("redirect:/my-tickets");
    }

    @GetMapping("/snacks/{ticketId}")
    public String showSnacks(Model model, @PathVariable Long ticketId) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        model.addAttribute("ticket", ticketId);
        return "snacks";
    }
}
