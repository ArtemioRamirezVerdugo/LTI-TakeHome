package com.oimzen.services.movieschallenge.controller;

import com.oimzen.services.movieschallenge.model.Movie;
import com.oimzen.services.movieschallenge.model.UserFavorite;
import com.oimzen.services.movieschallenge.model.UserVote;
import com.oimzen.services.movieschallenge.service.MovieService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @MockBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    @Mock private Movie movie1;
    @Mock private Movie movie2;
    @Mock private UserVote userVote;
    @Mock private UserFavorite userFavorite;

    @BeforeEach
    public void setup() {
        when(movie1.getTitle()).thenReturn("Test Movie 1");
        when(movie1.getId()).thenReturn(1);
        when(movie1.getReleaseYear()).thenReturn(2001);
        when(movie2.getTitle()).thenReturn("Test Movie 2");
        when(movie2.getId()).thenReturn(2);
        when(movie2.getReleaseYear()).thenReturn(2002);
        when(userVote.getMovieId()).thenReturn(1);
        when(userVote.getUserId()).thenReturn(1);
        when(userFavorite.getMovieId()).thenReturn(1);
        when(userFavorite.getUserId()).thenReturn(1);
    }

    @Test
    public void testGetAllMovies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(Arrays.asList(movie1, movie2));
        mockMvc.perform(get("/api/v1/movies").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Movie 1"))
                .andExpect(jsonPath("$[1].title").value("Test Movie 2"));
    }

    @Test
    public void testGetMovieById() throws Exception {
        when(movieService.getMovieById(1)).thenReturn(java.util.Optional.of(movie1));
        mockMvc.perform(get("/api/v1/movies/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie 1"));
    }

    @Test
    public void testGetMovieByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/movies/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetMoviesByYear() throws Exception {
        when(movieService.getMoviesByYear()).thenReturn(new HashMap<>() {{
            put(2001, Arrays.asList(movie1));
            put(2002, Arrays.asList(movie2));
        }});
        mockMvc.perform(get("/api/v1/movies/movies-by-year").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.2001[0].title").value("Test Movie 1"))
                .andExpect(jsonPath("$.2002[0].title").value("Test Movie 2"));
    }

    @Test
    public void testGetMoviesBySingleYear() throws Exception {
        when(movieService.getMoviesByYear(2001)).thenReturn(Arrays.asList(movie1));
        mockMvc.perform(get("/api/v1/movies/movies-by-year/2001").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Movie 1"));
    }

    @Test
    public void testAddMovie() throws Exception {
        when(movieService.addMovie(any(Movie.class))).thenReturn(movie1);
        mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                        .content(StringUtils.EMPTY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie 1"));
    }

    @Test
    public void testUpdateMovie() throws Exception {
        when(movieService.updateMovie(any(Movie.class))).thenReturn(movie1);
        mockMvc.perform(put("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                        .content(StringUtils.EMPTY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie 1"));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testVoteMovie() throws Exception {
        when(movieService.voteForMovie(any(UserVote.class))).thenReturn(userVote);
        mockMvc.perform(post("/api/v1/movies/vote").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    public void testDeleteVote() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/vote").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFavoriteMovie() throws Exception {
        when(movieService.favoriteMovie(any(UserFavorite.class))).thenReturn(userFavorite);
        mockMvc.perform(post("/api/v1/movies/favorite").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testDeleteFavorite() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/favorite").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
