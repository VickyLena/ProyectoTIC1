package com.wtfcinema.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "SNACK")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Snack {
    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private int price;

    @ManyToMany(mappedBy = "snacks", fetch = FetchType.EAGER)
    private List<Ticket> tickets = new LinkedList<>();

}
