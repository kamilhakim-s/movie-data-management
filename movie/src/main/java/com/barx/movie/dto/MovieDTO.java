package com.barx.movie.dto;

import com.barx.movie.model.Awards;
import com.barx.movie.model.ImdbInfo;
import com.barx.movie.model.TomatoesInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long id;
    private String title;
    private String plot;
    private List<String> genres;
    private Integer runtime;
    private String rated;
    private List<String> cast;
    private Integer numMflixComments;
    private String poster;
    private LocalDateTime lastUpdated;
    private List<String> languages;
    private List<String> directors;
    private List<String> writers;
    private Awards awards;
    private ImdbInfo imdb;
    private List<String> countries;
    private String type;
    private TomatoesInfo tomatoes;
}