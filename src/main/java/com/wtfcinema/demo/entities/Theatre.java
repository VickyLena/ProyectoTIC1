package com.wtfcinema.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "THEATRE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Theatre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUMBER")
    private int number;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<Screening> screenings = new LinkedList<Screening>();

    @ManyToOne
    @JoinColumn(name = "CINEMA")
    private Cinema cinema;
}
