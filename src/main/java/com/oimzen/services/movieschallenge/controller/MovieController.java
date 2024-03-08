package com.oimzen.services.movieschallenge.controller;

import com.oimzen.services.movieschallenge.exception.MovieNotFoundException;
import com.oimzen.services.movieschallenge.exception.UserNotFoundException;
import com.oimzen.services.movieschallenge.exception.util.ExceptionConstants;
import com.oimzen.services.movieschallenge.model.Movie;
import com.oimzen.services.movieschallenge.model.UserFavorite;
import com.oimzen.services.movieschallenge.model.UserVote;
import com.oimzen.services.movieschallenge.service.MovieService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP") //We own MovieService implementation, so we can consider it safe
public class MovieController {

    private final MovieService movieService;

    private final Logger logger = LoggerFactory.getLogger(MovieController.class);

    @GetMapping("/api/v1/movies")
    public List<Movie> getAllMovies() {
        logger.info("Received request to get all movies");
        return movieService.getAllMovies();
    }

    @GetMapping("/api/v1/movies/{id}")
    public Movie getMovieById(@PathVariable int id) {
        logger.info("Received request to get movie by id: {}", id);
        return movieService.getMovieById(id).orElseThrow(() ->
                new MovieNotFoundException(id));
    }

    @GetMapping("/api/v1/movies/movies-by-year")
    public ResponseEntity<Map<Integer, List<Movie>>> getMoviesByYear() {
        logger.info("Received request to get all movies grouped by year");
        return new ResponseEntity<>(movieService.getMoviesByYear(), HttpStatus.OK);
    }

    @GetMapping("/api/v1/movies/movies-by-year/{year}")
    public ResponseEntity<List<Movie>> getMoviesByYear(@PathVariable int year) {
        logger.info("Received request to get all movies by year: {}", year);
        return new ResponseEntity<>(movieService.getMoviesByYear(year), HttpStatus.OK);
    }

    @PostMapping("/api/v1/movies")
    public Movie addMovie(Movie movie) {
        logger.info("Received request to add movie: {}", movie);
        return movieService.addMovie(movie);
    }

    @PutMapping("/api/v1/movies")
    public Movie updateMovie(Movie movie) {
        logger.info("Received request to update movie: {}", movie);
        return movieService.updateMovie(movie);
    }

    @DeleteMapping("/api/v1/movies/{id}")
    @ApiResponse(responseCode = "204", description = "Movie deleted or nothing to delete")
    public ResponseEntity<Void> deleteMovie(@PathVariable int id) {
        logger.info("Received request to delete movie by id: {}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("api/v1/movies/vote")
    @ApiResponse(responseCode = "404", description = "Invalid input data")
    public UserVote voteForMovie(UserVote userVote) {
        logger.info("Received request to register vote: {}", userVote);
        return movieService.voteForMovie(userVote);
    }

    @DeleteMapping("api/v1/movies/vote")
    @ApiResponse(responseCode = "204", description = "Vote deleted or nothing to delete")
    public ResponseEntity<Void> deleteVote(UserVote userVote) {
        logger.info("Received request to delete vote: {}", userVote);
        movieService.deleteVote(userVote);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("api/v1/movies/favorite")
    @ApiResponse(responseCode = "404", description = "Invalid input data")
    public UserFavorite favoriteMovie(UserFavorite userFavorite) {
        logger.info("Received request to register user favorite movie: {}", userFavorite);
        return movieService.favoriteMovie(userFavorite);
    }

    @DeleteMapping("api/v1/movies/favorite")
    @ApiResponse(responseCode = "204", description = "Favorite deleted or nothing to delete")
    public ResponseEntity<Void> deleteFavorite(UserFavorite userFavorite) {
        logger.info("Received request to delete user favorite movie: {}", userFavorite);
        movieService.deleteFavorite(userFavorite);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMovieNotFoundException(MovieNotFoundException ex) {
        return getMapResponseEntity(ex.getMessage(), ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        return getMapResponseEntity(ex.getMessage(), ex);
    }

    private <T extends Exception> ResponseEntity<Map<String, Object>> getMapResponseEntity(String message, T ex) {
        final Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(ExceptionConstants.EXCEPTION_ATTRIBUTE_NAME, ex.getClass().getSimpleName());
        errorAttributes.put(ExceptionConstants.MESSAGE_ATTRIBUTE_NAME, message);
        errorAttributes.put(ExceptionConstants.TIMESTAMP_ATTRIBUTE_NAME, LocalDateTime.now());
        errorAttributes.put(ExceptionConstants.STATUS_ATTRIBUTE_NAME, HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorAttributes, HttpStatus.NOT_FOUND);
    }
}
