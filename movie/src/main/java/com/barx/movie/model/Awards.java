package com.barx.movie.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Awards object representing movie awards and nominations
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Awards {
    /**
     * The number of awards won
     */
    private Integer wins;
    
    /**
     * The number of awards the movie was nominated for
     */
    private Integer nominations;
    
    /**
     * A text summary of the awards and nominations
     */
    private String text;
}