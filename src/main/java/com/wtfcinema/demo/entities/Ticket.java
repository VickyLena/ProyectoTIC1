package com.wtfcinema.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "TICKET")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SEAT")
    private String seat;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TICKET_SNACK", joinColumns = @JoinColumn(name = "TICKET_ID"), inverseJoinColumns = @JoinColumn(name = "SNACK_ID"))
    @Builder.Default
    private List<Snack> snacks = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "SCREENING")
    private Screening screening;

    public void addSnacks(Snack newSnack) {
        this.snacks.add(newSnack);
    }
}
