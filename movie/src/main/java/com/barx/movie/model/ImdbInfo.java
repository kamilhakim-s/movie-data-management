package com.barx.movie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IMDb information object
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImdbInfo {
    /**
     * The IMDb rating
     */
    private Double rating;
    
    /**
     * The number of votes on IMDb
     */
    private Integer votes;
    
    /**
     * The IMDb ID of the movie
     */
    @Column(name = "imdb_id")
    private String id;
}
