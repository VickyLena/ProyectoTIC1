package com.wtfcinema.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "CINEMA")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cinema {
    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE_NUMBER")
    private int phoneNumber;

    @Column(name = "ADDRESS")
    private String address;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Theatre> theatres = new LinkedList<Theatre>();
}
