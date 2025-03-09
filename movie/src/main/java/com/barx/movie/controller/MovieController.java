package com.barx.movie.controller;

import com.barx.movie.dto.MovieDTO;
import com.barx.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    
    private final MovieService movieService;
    
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String plotKeyword) {
        
        List<MovieDTO> results;
        
        if (title != null && !title.isEmpty()) {
            results = movieService.searchMoviesByTitle(title);
        } else if (genre != null && !genre.isEmpty()) {
            results = movieService.searchMoviesByGenre(genre);
        } else if (director != null && !director.isEmpty()) {
            results = movieService.searchMoviesByDirector(director);
        } else if (actor != null && !actor.isEmpty()) {
            results = movieService.searchMoviesByActor(actor);
        } else if (plotKeyword != null && !plotKeyword.isEmpty()) {
            results = movieService.searchMoviesByPlotKeyword(plotKeyword);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(results);
    }
    
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody MovieDTO movieDTO) {
        MovieDTO createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @RequestBody MovieDTO movieDTO) {
        return movieService.updateMovie(id, movieDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieService.deleteMovie(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}