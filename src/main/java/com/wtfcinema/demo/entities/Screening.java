package com.wtfcinema.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "SCREENING")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> takenSeats = new LinkedList<Ticket>();

    ////////////////////////////////////////
    @ManyToOne
    @JoinColumn(name = "THEATRE_ID")
    private Theatre theatre;
    ///////////////////////////////////////
    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;
    /////////////////////////////////////////////


    public String getDateLindo(){
        return dateTime.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm"));
    }
    public String getDateLindo2(){
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm"));
    }

}
