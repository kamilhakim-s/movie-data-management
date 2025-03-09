package com.barx.movie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(length = 2000)
    private String plot;
    
    @ElementCollection
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private List<String> genres;
    
    private Integer runtime;
    
    private String rated;
    
    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor")
    private List<String> cast;
    
    private Integer numMflixComments;
    
    private String poster;
    
    private LocalDateTime lastUpdated;
    
    @ElementCollection
    @CollectionTable(name = "movie_languages", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "language")
    private List<String> languages;
    
    @ElementCollection
    @CollectionTable(name = "movie_directors", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "director")
    private List<String> directors;
    
    @ElementCollection
    @CollectionTable(name = "movie_writers", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "writer")
    private List<String> writers;
    
    @Embedded
    private Awards awards;
    
    @Embedded
    private ImdbInfo imdb;
    
    @ElementCollection
    @CollectionTable(name = "movie_countries", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "country")
    private List<String> countries;
    
    private String type;
    
    @Embedded
    private TomatoesInfo tomatoes;
}
