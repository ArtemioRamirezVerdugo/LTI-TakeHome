package com.oimzen.services.movieschallenge.service;

import com.oimzen.services.movieschallenge.dao.MovieDao;
import com.oimzen.services.movieschallenge.exception.MovieNotFoundException;
import com.oimzen.services.movieschallenge.exception.UserNotFoundException;
import com.oimzen.services.movieschallenge.model.Movie;
import com.oimzen.services.movieschallenge.model.UserFavorite;
import com.oimzen.services.movieschallenge.model.UserVote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    private MovieDao movieDao;
    private MovieService movieService;

    @Mock private Movie movie;
    @Mock private UserVote userVote;
    @Mock private UserFavorite userFavorite;

    @BeforeEach
    public void setUp() {
        movieDao = mock(MovieDao.class);
        movieService = new MovieService(movieDao);
    }

    @Test
    public void testGetAllMovies() {
        when(movieDao.getAllMovies()).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), movieService.getAllMovies());
    }

    @Test
    public void testGetMovieById() {
        when(movieDao.getMovieById(1)).thenReturn(Optional.of(movie));
        assertEquals(Optional.of(movie), movieService.getMovieById(1));
    }

    @Test
    public void testAddMovie() {
        when(movieDao.addMovie(movie)).thenReturn(movie);
        assertEquals(movie, movieService.addMovie(movie));
    }

    @Test
    public void testUpdateMovie() {
        when(movieDao.updateMovie(movie)).thenReturn(movie);
        assertEquals(movie, movieService.updateMovie(movie));
    }

    @Test
    public void testDeleteMovie() {
        movieService.deleteMovie(1);
        verify(movieDao, times(1)).deleteMovie(1);
    }

    @Test
    public void testGetMoviesByYear() {
        when(movieDao.getMoviesByYear()).thenReturn(Collections.emptyMap());
        assertEquals(Collections.emptyMap(), movieService.getMoviesByYear());
    }

    @Test
    public void testGetMoviesBySingleYear() {
        when(movieDao.getMoviesByYear(2001)).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), movieService.getMoviesByYear(2001));
    }

    @Test
    public void testVoteForMovie_movieNotFound() {
        when(movieDao.movieExists(anyInt())).thenReturn(false);
        assertThrows(MovieNotFoundException.class, () -> movieService.voteForMovie(userVote));
    }

    @Test
    public void testVoteForMovie_userNotFound() {
        when(movieDao.movieExists(anyInt())).thenReturn(true);
        when(movieDao.userExists(anyInt())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> movieService.voteForMovie(userVote));
    }

    @Test
    public void testVoteForMovie_voteExists() {
        when(movieDao.movieExists(anyInt())).thenReturn(true);
        when(movieDao.userExists(anyInt())).thenReturn(true);
        when(movieDao.voteExists(userVote)).thenReturn(true);
        when(movieDao.updateVoteForMovie(userVote)).thenReturn(userVote);
        assertEquals(userVote, movieService.voteForMovie(userVote));
    }

    @Test
    public void testVoteForMovie_voteDoesNotExist() {
        when(movieDao.movieExists(anyInt())).thenReturn(true);
        when(movieDao.userExists(anyInt())).thenReturn(true);
        when(movieDao.voteExists(userVote)).thenReturn(false);
        when(movieDao.voteForMovie(userVote)).thenReturn(userVote);
        assertEquals(userVote, movieService.voteForMovie(userVote));
    }

    @Test
    public void testDeleteVote() {
        movieService.deleteVote(userVote);
        verify(movieDao, times(1)).deleteVote(userVote);
    }

    @Test
    public void testFavoriteMovie_movieNotFound() {
        when(movieDao.movieExists(anyInt())).thenReturn(false);
        assertThrows(MovieNotFoundException.class, () -> movieService.favoriteMovie(userFavorite));
    }

    @Test
    public void testFavoriteMovie_userNotFound() {
        when(movieDao.movieExists(anyInt())).thenReturn(true);
        when(movieDao.userExists(anyInt())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> movieService.favoriteMovie(userFavorite));
    }

    @Test
    public void testFavoriteMovie_favoriteExists() {
        when(movieDao.movieExists(anyInt())).thenReturn(true);
        when(movieDao.userExists(anyInt())).thenReturn(true);
        when(movieDao.favoriteExists(userFavorite)).thenReturn(true);
        assertEquals(userFavorite, movieService.favoriteMovie(userFavorite));
    }

    @Test
    public void testFavoriteMovie_favoriteDoesNotExist() {
        when(movieDao.movieExists(anyInt())).thenReturn(true);
        when(movieDao.userExists(anyInt())).thenReturn(true);
        when(movieDao.favoriteExists(userFavorite)).thenReturn(false);
        when(movieDao.favoriteMovie(userFavorite)).thenReturn(userFavorite);
        assertEquals(userFavorite, movieService.favoriteMovie(userFavorite));
    }

    @Test
    public void testDeleteFavorite() {
        movieService.deleteFavorite(userFavorite);
        verify(movieDao, times(1)).deleteFavorite(userFavorite);
    }
}