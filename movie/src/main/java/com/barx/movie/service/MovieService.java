package com.barx.movie.service;

import com.barx.movie.dto.MovieDTO;
import com.barx.movie.model.Movie;
import com.barx.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    
    private final MovieRepository movieRepository;
    
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<MovieDTO> getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public List<MovieDTO> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MovieDTO> searchMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MovieDTO> searchMoviesByDirector(String director) {
        return movieRepository.findByDirector(director).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MovieDTO> searchMoviesByActor(String actor) {
        return movieRepository.findByActor(actor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MovieDTO> searchMoviesByPlotKeyword(String keyword) {
        return movieRepository.findByPlotKeyword(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = convertToEntity(movieDTO);
        movie.setLastUpdated(LocalDateTime.now());
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }
    
    @Transactional
    public Optional<MovieDTO> updateMovie(Long id, MovieDTO movieDTO) {
        return movieRepository.findById(id)
                .map(existingMovie -> {
                    Movie movie = convertToEntity(movieDTO);
                    movie.setId(id);
                    movie.setLastUpdated(LocalDateTime.now());
                    Movie updatedMovie = movieRepository.save(movie);
                    return convertToDTO(updatedMovie);
                });
    }
    
    @Transactional
    public boolean deleteMovie(Long id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private MovieDTO convertToDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        BeanUtils.copyProperties(movie, dto);
        return dto;
    }
    
    private Movie convertToEntity(MovieDTO dto) {
        Movie movie = new Movie();
        BeanUtils.copyProperties(dto, movie);
        return movie;
    }
}