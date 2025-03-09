package com.barx.movie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * Rotten Tomatoes information object
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TomatoesInfo {
    /**
     * Viewer rating
     */
    private Double viewerRating;
    
    /**
     * Number of viewer reviews
     */
    private Integer viewerReviews;
    
    /**
     * Critic rating
     */
    private Double criticRating;
    
    /**
     * Number of critic reviews
     */
    private Integer reviewsCount;
    
    /**
     * Rotten Tomatoes consensus text
     */
    private String consensus;
    
    /**
     * Audience score percentage
     */
    private Integer audienceScore;
    
    /**
     * DVD release date
     */
    private LocalDateTime dvdReleaseDate;
    
    /**
     * Production company
     */
    private String production;
    
    /**
     * Last updated timestamp
     */
    @Column(name = "tomatoes_last_updated")
    private LocalDateTime lastUpdated;
    
    /**
     * Number of "fresh" ratings
     */
    private Integer fresh;
    
    /**
     * Number of "rotten" ratings
     */
    private Integer rotten;
    
    /**
     * Rotten Tomatoes website link
     */
    private String website;
    
    /**
     * Box office performance
     */
    private String boxOffice;
}
