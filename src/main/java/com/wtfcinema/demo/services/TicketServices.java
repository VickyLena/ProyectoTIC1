package com.wtfcinema.demo.services;

import com.wtfcinema.demo.entities.Ticket;
import com.wtfcinema.demo.repository.TicketRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TicketServices {
    @Autowired
    private TicketRep ticketRep;

    public Optional<Ticket> findById(Long id) { return ticketRep.findByIdWithSnack(id);}

    public void registerNewTicket(Ticket ticket) {
        ticketRep.save(ticket);
    }

    @Transactional
    public void deleteById(Long id) {
        ticketRep.deleteById(id);
    }

    @Transactional
    public void editTicket(Ticket ticket) {
        ticketRep.save(ticket);
    }
}
