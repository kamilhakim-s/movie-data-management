package com.barx.movie.repository;

import com.barx.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    List<Movie> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g) = LOWER(:genre)")
    List<Movie> findByGenre(@Param("genre") String genre);
    
    @Query("SELECT m FROM Movie m JOIN m.directors d WHERE LOWER(d) = LOWER(:director)")
    List<Movie> findByDirector(@Param("director") String director);
    
    @Query("SELECT m FROM Movie m JOIN m.cast c WHERE LOWER(c) = LOWER(:actor)")
    List<Movie> findByActor(@Param("actor") String actor);
    
    @Query("SELECT m FROM Movie m WHERE LOWER(m.plot) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> findByPlotKeyword(@Param("keyword") String keyword);
    
    List<Movie> findByCountriesContaining(String country);
    
    List<Movie> findByRated(String rated);
    
    @Query("SELECT m FROM Movie m WHERE m.runtime <= :maxRuntime")
    List<Movie> findByMaxRuntime(@Param("maxRuntime") Integer maxRuntime);
    
    @Query("SELECT m FROM Movie m WHERE m.imdb.rating >= :minRating")
    List<Movie> findByMinImdbRating(@Param("minRating") Double minRating);
}