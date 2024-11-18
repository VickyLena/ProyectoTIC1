package com.wtfcinema.demo.controller;

import com.wtfcinema.demo.entities.*;
import com.wtfcinema.demo.services.ScreeningServices;
import com.wtfcinema.demo.services.SnackServices;
import com.wtfcinema.demo.services.TicketServices;
import com.wtfcinema.demo.services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

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
        model.addAttribute("screening_id", screening_id);
        User loggedInUser = (User) session.getAttribute("USER");
        if (loggedInUser == null) {
            Employee userAdmin = (Employee) session.getAttribute("EMPLOYEE");
            model.addAttribute("employee", userAdmin);
            model.addAttribute("user", null);
        }else{
            model.addAttribute("user", loggedInUser);
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime screeningDateTime = screening.getDateTime();

        long monthsDifference = ChronoUnit.MONTHS.between(currentDateTime.toLocalDate(), screeningDateTime.toLocalDate());
        if (monthsDifference > 6) {
            model.addAttribute("hay_que_pagar", true);
        }else{
            model.addAttribute("hay_que_pagar", false);
        }
        return "seats";
    }

    ////PAYMENT////

    @Transactional
    @GetMapping({"/payment-method/m/{screening_id}/{seats}","/payment-method/s/{ticketId}"})
    public String selectPaymentMethod(HttpSession session, Model model, @PathVariable(required = false) Long screening_id, @PathVariable(required = false) String ticketId,
                                      @PathVariable(required = false) String seats){


        model.addAttribute("screening_id", screening_id);
        model.addAttribute("seats", seats);

        User loggedInUser = (User) session.getAttribute("USER");
        if (loggedInUser == null) {
            Employee userAdmin = (Employee) session.getAttribute("EMPLOYEE");
            model.addAttribute("employee", userAdmin);
            model.addAttribute("user", null);
        }else{
            if (loggedInUser.getCardNumber() != null) {
                model.addAttribute("card", loggedInUser.getCardNumber());
            }
        }

        if(model.getAttribute("screening_id") == null) {
            String snack = (String) session.getAttribute("snack");
            System.out.println("Snack desde sesión: " + snack);

            model.addAttribute("snack", snack);
            model.addAttribute("ticketId", ticketId);
//            session.removeAttribute("snack");
        }

        return "paymentMethod";
    }

    @Transactional
    @GetMapping({"/new-card/m/{screening_id}/{seats}", "/new-card/s/{ticketId}"})
    public String newCard(Model model, @PathVariable(required = false) Long screening_id, @PathVariable(required = false) Long ticketId,
                          @PathVariable(required = false) String seats){
        System.out.println("screening_id: " + screening_id);
        model.addAttribute("screening_id", screening_id);
        model.addAttribute("ticketId", ticketId);
        model.addAttribute("seats", seats);
        String snack = (String) session.getAttribute("snack");
        model.addAttribute("snack", snack);

        return "newCard";}

    @Transactional
    @PostMapping("/addCard")
    public String addCard(Model model, @RequestParam Long cardNumber, @RequestParam(required = false) Boolean permanent, @RequestParam(required = false) Long ticketId,
                          @RequestParam(required = false) Long screening_id, @RequestParam(required = false) String seats, @RequestParam(required = false) String snack, RedirectAttributes redirectAttributes){

        System.out.println("LLEGAAAAAAAAAaaa"+screening_id);
        int length = String.valueOf(cardNumber).length();
        if (length!= 16){
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: El numero de tarjeta debe tener largo 16");
            if(model.getAttribute("screening_id") != null){
                return "redirect:/new-card/m/" + screening_id + "/" + seats;
            }
            return "redirect:/new-card/s/" + ticketId;
        }
        System.out.println("LLEGAAAAAAAAAaaa");
//        if (permanent == null) {
//            System.out.println("LLEGAAAAAAAAAaaa");
//            permanent = false;
//        }
        System.out.println(permanent);
        if (permanent){
            User loggedInUser = (User) session.getAttribute("USER");
            userServices.saveUserNewCard(loggedInUser,cardNumber);
        }
        System.out.println(snack);
        if(screening_id!=null){
            System.out.println("puta"+screening_id);
            model.addAttribute("screening_id", screening_id);
            return "redirect:/payed/m/" + screening_id + "/" + seats;
        }
        return "redirect:/payed/s/" + ticketId;
    }

    @Transactional
    @GetMapping({"/payed/m/{screening_id}/{seats}","/payed/s/{ticketId}"})
    public String confirmPayment(Model model, @PathVariable(required = false) Long screening_id,
                                 @PathVariable(required = false) String seats,
                                 @PathVariable(required = false) String ticketId){
        System.out.println("screening_id: " + screening_id);
        if(screening_id == null){
            System.out.println(model.getAttribute("snack"));
            return "redirect:/add-snack/{ticketId}";
        }else{

            Optional<Screening> screening = screeningServices.findById(screening_id);
            List<String> selectedSeats = List.of(seats.split(","));
            User loggedInUser = (User) session.getAttribute("USER");
            if (loggedInUser == null) {
                Employee userAdmin = (Employee) session.getAttribute("EMPLOYEE");
                model.addAttribute("employee", userAdmin);
                model.addAttribute("user", null);

                for (String number : selectedSeats) {
                    Ticket newTicket = Ticket.builder()
                            .seat(number)
                            .employee(userAdmin)
                            .screening(screening.get())
                            .build();
                    ticketServices.registerNewTicket(newTicket);
                }

            }else {
                for (String number : selectedSeats) {
                    Ticket newTicket = Ticket.builder()
                            .seat(number)
                            .user(loggedInUser)
                            .screening(screening.get())
                            .build();
                    ticketServices.registerNewTicket(newTicket);
                }
                model.addAttribute("user", loggedInUser);
            }
            return "redirect:/my-tickets";
        }
    }

    @Transactional
    @GetMapping("/add-snack/{ticketId}")
    public String addSnack(Model model, @PathVariable Long ticketId) {
        Optional<Ticket> ticketOptional = ticketServices.findById(ticketId);
        String snack = (String) session.getAttribute("snack");

        List<Snack> snackObjectList = new ArrayList<>();
        List<String> snackList = Arrays.asList(snack.split(","));

        for (String snacki : snackList) {
            Optional<Snack> snackObj = snackServices.findByName(snacki);
            snackObjectList.add(snackObj.get());
        }
        snackObjectList.addAll(ticketOptional.get().getSnacks());
        ticketOptional.get().setSnacks(snackObjectList);
        ticketServices.editTicket(ticketOptional.get());
        session.setAttribute("snack", null);
        return "redirect:/my-tickets";
    }


    ////////////////SNACKS//////////////////////7
    @Transactional
    @PostMapping("/snack-payment-method/{ticketId}")
    public ResponseEntity<Map<String, String>> selectSnackPaymentMethod(
            HttpSession session,
            @PathVariable Long ticketId,
            @RequestBody List<String> snackList, Model model) {

        System.out.println("Lista de snacks: " + snackList);
        String snackListAsString = String.join(",", snackList);
        session.setAttribute("snack", snackListAsString);

        model.addAttribute("screening_id", null);
        model.addAttribute("seat", null);

        String redi = "/payment-method/s/" + ticketId;

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", redi);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/snacks/{ticketId}")
    public String showSnacks(Model model, @PathVariable Long ticketId) {
        List<Snack> snackList = snackServices.getAllSnacks();
        model.addAttribute("snacks", snackList);
        model.addAttribute("ticket", ticketId);
        return "snacks";
    }

}
