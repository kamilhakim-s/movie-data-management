package com.barx.movie.config;

import com.barx.movie.model.*;
import com.barx.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final MovieRepository movieRepository;
    private Logger logger = Logger.getLogger(DataLoader.class.getName());

    @Bean
    @Profile("dev")  // Only run in development profile
    public CommandLineRunner loadSampleData() {
        return args -> {
            if (movieRepository.count() == 0) {
                logger.info("Loading sample movie data...");
                
                Movie movie1 = Movie.builder()
                        .title("The Shawshank Redemption")
                        .plot("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.")
                        .genres(Arrays.asList("Drama", "Crime"))
                        .runtime(142)
                        .rated("R")
                        .cast(Arrays.asList("Tim Robbins", "Morgan Freeman", "Bob Gunton", "William Sadler"))
                        .numMflixComments(124)
                        .poster("https://example.com/shawshank-poster.jpg")
                        .lastUpdated(LocalDateTime.now())
                        .languages(List.of("English"))
                        .directors(List.of("Frank Darabont"))
                        .writers(Arrays.asList("Stephen King", "Frank Darabont"))
                        .awards(Awards.builder()
                                .wins(28)
                                .nominations(18)
                                .text("Nominated for 7 Oscars. Won 11 other awards & 18 nominations.")
                                .build())
                        .imdb(ImdbInfo.builder()
                                .rating(9.3)
                                .votes(2603497)
                                .id("tt0111161")
                                .build())
                        .countries(List.of("USA"))
                        .type("movie")
                        .tomatoes(TomatoesInfo.builder()
                                .viewerRating(4.5)
                                .viewerReviews(97642)
                                .criticRating(4.8)
                                .reviewsCount(82)
                                .consensus("The Shawshank Redemption is an uplifting, deeply satisfying prison drama with sensitive direction and fine performances.")
                                .audienceScore(98)
                                .dvdReleaseDate(LocalDateTime.of(1997, 12, 2, 0, 0))
                                .production("Columbia Pictures")
                                .lastUpdated(LocalDateTime.now())
                                .fresh(80)
                                .rotten(2)
                                .website("https://www.warnerbros.com/movies/shawshank-redemption")
                                .boxOffice("$28.3 million")
                                .build())
                        .build();
                
                Movie movie2 = Movie.builder()
                        .title("The Godfather")
                        .plot("The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.")
                        .genres(Arrays.asList("Crime", "Drama"))
                        .runtime(175)
                        .rated("R")
                        .cast(Arrays.asList("Marlon Brando", "Al Pacino", "James Caan", "Robert Duvall"))
                        .numMflixComments(97)
                        .poster("https://example.com/godfather-poster.jpg")
                        .lastUpdated(LocalDateTime.now())
                        .languages(Arrays.asList("English", "Italian", "Latin"))
                        .directors(List.of("Francis Ford Coppola"))
                        .writers(Arrays.asList("Mario Puzo", "Francis Ford Coppola"))
                        .awards(Awards.builder()
                                .wins(38)
                                .nominations(12)
                                .text("Won 3 Oscars. 38 wins & 12 nominations total.")
                                .build())
                        .imdb(ImdbInfo.builder()
                                .rating(9.2)
                                .votes(1714835)
                                .id("tt0068646")
                                .build())
                        .countries(List.of("USA"))
                        .type("movie")
                        .tomatoes(TomatoesInfo.builder()
                                .viewerRating(4.7)
                                .viewerReviews(82300)
                                .criticRating(4.9)
                                .reviewsCount(92)
                                .consensus("One of Hollywood's greatest critical and commercial successes, The Godfather gets everything right; not only did the movie transcend expectations, it established new benchmarks for American cinema.")
                                .audienceScore(98)
                                .dvdReleaseDate(LocalDateTime.of(2001, 10, 9, 0, 0))
                                .production("Paramount Pictures")
                                .lastUpdated(LocalDateTime.now())
                                .fresh(89)
                                .rotten(3)
                                .website("https://www.paramountmovies.com/movies/the-godfather")
                                .boxOffice("$134.8 million")
                                .build())
                        .build();
                
                Movie movie3 = Movie.builder()
                        .title("Pulp Fiction")
                        .plot("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.")
                        .genres(Arrays.asList("Crime", "Drama", "Thriller"))
                        .runtime(154)
                        .rated("R")
                        .cast(Arrays.asList("John Travolta", "Uma Thurman", "Samuel L. Jackson", "Bruce Willis"))
                        .numMflixComments(78)
                        .poster("https://example.com/pulp-fiction-poster.jpg")
                        .lastUpdated(LocalDateTime.now())
                        .languages(List.of("English"))
                        .directors(List.of("Quentin Tarantino"))
                        .writers(Arrays.asList("Quentin Tarantino", "Roger Avary"))
                        .awards(Awards.builder()
                                .wins(62)
                                .nominations(44)
                                .text("Won Oscar for Best Original Screenplay. 62 wins & 44 nominations total.")
                                .build())
                        .imdb(ImdbInfo.builder()
                                .rating(8.9)
                                .votes(1862147)
                                .id("tt0110912")
                                .build())
                        .countries(List.of("USA"))
                        .type("movie")
                        .tomatoes(TomatoesInfo.builder()
                                .viewerRating(4.6)
                                .viewerReviews(75832)
                                .criticRating(4.7)
                                .reviewsCount(79)
                                .consensus("One of the most influential films of the 1990s, Pulp Fiction is a delirious post-modern mix of neo-noir thrills, pitch-black humor, and pop-culture touchstones.")
                                .audienceScore(96)
                                .dvdReleaseDate(LocalDateTime.of(1998, 5, 19, 0, 0))
                                .production("Miramax Films")
                                .lastUpdated(LocalDateTime.now())
                                .fresh(75)
                                .rotten(4)
                                .website("https://www.miramax.com/movie/pulp-fiction")
                                .boxOffice("$107.9 million")
                                .build())
                        .build();
                
                // Adding a more recent movie
                Movie movie4 = Movie.builder()
                        .title("Parasite")
                        .plot("Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan.")
                        .genres(Arrays.asList("Drama", "Thriller", "Comedy"))
                        .runtime(132)
                        .rated("R")
                        .cast(Arrays.asList("Song Kang-ho", "Lee Sun-kyun", "Cho Yeo-jeong", "Choi Woo-shik", "Park So-dam"))
                        .numMflixComments(42)
                        .poster("https://example.com/parasite-poster.jpg")
                        .lastUpdated(LocalDateTime.now())
                        .languages(Arrays.asList("Korean", "English"))
                        .directors(List.of("Bong Joon Ho"))
                        .writers(Arrays.asList("Bong Joon Ho", "Han Jin-won"))
                        .awards(Awards.builder()
                                .wins(304)
                                .nominations(262)
                                .text("Won 4 Oscars including Best Picture and Best Director. 304 wins & 262 nominations total.")
                                .build())
                        .imdb(ImdbInfo.builder()
                                .rating(8.6)
                                .votes(732946)
                                .id("tt6751668")
                                .build())
                        .countries(Arrays.asList("South Korea", "USA"))
                        .type("movie")
                        .tomatoes(TomatoesInfo.builder()
                                .viewerRating(4.5)
                                .viewerReviews(21500)
                                .criticRating(4.9)
                                .reviewsCount(457)
                                .consensus("An urgent, brilliantly layered look at timely social themes, Parasite finds writer-director Bong Joon Ho in near-total command of his craft.")
                                .audienceScore(90)
                                .dvdReleaseDate(LocalDateTime.of(2020, 1, 28, 0, 0))
                                .production("Neon")
                                .lastUpdated(LocalDateTime.now())
                                .fresh(449)
                                .rotten(8)
                                .website("https://www.parasite-movie.com/")
                                .boxOffice("$53.4 million")
                                .build())
                        .build();
                
                movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4));
                logger.info("Sample data loaded successfully!");
            }
        };
    }
}