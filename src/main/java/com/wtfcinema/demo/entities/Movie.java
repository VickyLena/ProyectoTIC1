package com.wtfcinema.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "MOVIE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "RELEASE_DATE")
    private Date releaseDate;

    @Column(name = "DIRECTOR")
    private String director;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "MOVIE_GENRES")
    @Column(name = "GENRE")
    private List<String> genres;

    @Column(name = "AGE_RESTRICTION")
    private int ageRestriction;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Screening> screenings = new LinkedList<Screening>();

}
