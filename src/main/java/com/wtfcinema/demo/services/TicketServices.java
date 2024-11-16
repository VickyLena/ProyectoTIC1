package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.repository.TicketRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketServices {
    @Autowired
    private TicketRep ticketRep;

    public Optional<Ticket> findById(Long id) { return ticketRep.findById(id);}

    public void registerNewTicket(Ticket ticket) {
        if (ticketRep.findById(ticket.getId()).isPresent()) {
            throw new RuntimeException("Error al registrar el ticket.");
        }
        ticketRep.save(ticket);
    }

    public void deleteById(Long id) {
        ticketRep.deleteById(id);
    }
}
