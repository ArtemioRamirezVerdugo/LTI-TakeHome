package com.oimzen.services.movieschallenge.dao;

import com.oimzen.services.movieschallenge.dao.mapper.MovieRowMapper;
import com.oimzen.services.movieschallenge.dao.util.DaoConstants;
import com.oimzen.services.movieschallenge.model.Movie;
import com.oimzen.services.movieschallenge.model.UserFavorite;
import com.oimzen.services.movieschallenge.model.UserVote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieDaoTest {


    private MovieDao movieDao;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private Movie movie;
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private UserVote userVote;
    @Mock private UserFavorite userFavorite;
    @BeforeEach
    public void setUp() {
        movieDao = new MovieDao(jdbcTemplate);
    }

    @Test
    public void testGetAllMovies() {
        when(jdbcTemplate.query(anyString(), isA(MovieRowMapper.class))).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), movieDao.getAllMovies());
    }

    @Test
    public void testGetMovieById() {
        int movieId = 1;
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(MovieRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    verify(ps).setInt(1, movieId);
                    return Collections.singletonList(movie);
                });
        assertEquals(Optional.of(movie), movieDao.getMovieById(movieId));
    }

    @Test
    public void testGetMovieById_notFound() {
        int movieId = 1;
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(MovieRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    verify(ps).setInt(1, movieId);
                    return Collections.emptyList();
                });
        assertEquals(Optional.empty(), movieDao.getMovieById(movieId));
    }

    @Test
    public void testAddMovie() {
        PreparedStatement ps = mock(PreparedStatement.class);
        try (Connection connection = mock(Connection.class)) {
            when(connection.prepareStatement(anyString(), any(String[].class))).thenReturn(ps);

            doAnswer(invocation -> {
                PreparedStatementCreator creator = invocation.getArgument(0);
                KeyHolder keyHolder = invocation.getArgument(1);
                PreparedStatement psMock = creator.createPreparedStatement(connection);
                psMock.setInt(1, movie.getDownvotes());
                psMock.setString(2, movie.getImageUrl());
                psMock.setInt(3, movie.getFavoriteCount());
                psMock.setInt(4, movie.getScore());
                psMock.setInt(5, movie.getReleaseYear());
                psMock.setString(6, movie.getTitle());
                psMock.setInt(7, movie.getUpvotes());
                psMock.setString(8, movie.getDetails());
                // Set the key on the KeyHolder that was passed to the update method
                keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
                return 1;
            }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

            Movie result = movieDao.addMovie(movie);
            assertEquals(movie.getId(), result.getId());
        } catch (SQLException e) {
            // Handle any exceptions
        }
    }

    @Test
    public void testAddMovie_noKey() {
        assertThrows(RuntimeException.class, () -> {
            when(connection.prepareStatement(anyString(), any(String[].class))).thenReturn(ps);

            doAnswer(invocation -> {
                PreparedStatementCreator creator = invocation.getArgument(0);
                creator.createPreparedStatement(connection);
                // Do not set the key on the KeyHolder that was passed to the update method
                return 1;
            }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

            movieDao.addMovie(movie);
        });
    }

    @Test
    public void testUpdateMovie() {
        assertEquals(movie, movieDao.updateMovie(movie));
    }

    @Test
    public void deleteMovie() {
        int movieId = 1;
        movieDao.deleteMovie(movieId);
        verify(jdbcTemplate).update(anyString(), eq(movieId));
    }

    @Test
    public void testGetMoviesByYear() {
        // Arrange
        Movie movie1 = mock(Movie.class);
        when(movie1.getReleaseYear()).thenReturn(2000);
        Movie movie2 = mock(Movie.class);
        when(movie2.getReleaseYear()).thenReturn(2001);
        List<Movie> movies = Arrays.asList(movie1, movie2);
        when(jdbcTemplate.query(eq(DaoConstants.SQL_QUERY_SELECT_ALL_MOVIES), any(MovieRowMapper.class))).thenReturn(movies);

        // Act
        Map<Integer, List<Movie>> result = movieDao.getMoviesByYear();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(2000));
        assertTrue(result.containsKey(2001));
        assertEquals(1, result.get(2000).size());
        assertEquals(1, result.get(2001).size());
    }

    @Test
    public void testGetMoviesByYear_empty() {
        when(jdbcTemplate.query(eq(DaoConstants.SQL_QUERY_SELECT_ALL_MOVIES), any(MovieRowMapper.class))).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyMap(), movieDao.getMoviesByYear());
    }

    @Test
    public void testGetMoviesBySingleYear() {
        int year = 2000;
        when(jdbcTemplate.query(eq(DaoConstants.SQL_QUERY_SELECT_MOVIES_WITH_YEAR), any(PreparedStatementSetter.class), any(MovieRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    verify(ps).setInt(1, year);
                    return Collections.singletonList(movie);
                });
        assertEquals(Collections.singletonList(movie), movieDao.getMoviesByYear(year));
    }

    @Test
    public void testGetMoviesBySingleYear_empty() {
        int year = 2000;
        when(jdbcTemplate.query(eq(DaoConstants.SQL_QUERY_SELECT_MOVIES_WITH_YEAR), any(PreparedStatementSetter.class), any(MovieRowMapper.class)))
                .thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), movieDao.getMoviesByYear(year));
    }

    @Test
    public void testVoteForMovie() {
        assertEquals(userVote, movieDao.voteForMovie(userVote));
    }

    @Test
    public void testVoteExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(BeanPropertyRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    return Collections.singletonList(userVote);
                });
        assertTrue(movieDao.voteExists(userVote));
    }

    @Test
    public void testVoteExists_notExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        assertFalse(movieDao.voteExists(userVote));
    }

    @Test
    public void testUpdateVoteForMovie() {
        assertEquals(userVote, movieDao.updateVoteForMovie(userVote));
    }

    @Test
    public void testDeleteVote() {
        movieDao.deleteVote(userVote);
        verify(jdbcTemplate).update(DaoConstants.SQL_QUERY_DELETE_USERVOTE, userVote.getMovieId(), userVote.getUserId());
    }

    @Test
    public void testFavoriteMovie() {
        assertEquals(userFavorite, movieDao.favoriteMovie(userFavorite));
    }

    @Test
    public void testFavoriteExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(BeanPropertyRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    return Collections.singletonList(userFavorite);
                });
        assertTrue(movieDao.favoriteExists(userFavorite));
    }

    @Test
    public void testFavoriteExists_notExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        assertFalse(movieDao.favoriteExists(userFavorite));
    }

    @Test
    public void testDeleteFavorite() {
        movieDao.deleteFavorite(userFavorite);
        verify(jdbcTemplate).update(DaoConstants.SQL_QUERY_DELETE_USERFAVORITE, userFavorite.getMovieId(), userFavorite.getUserId());
    }

    @Test
    public void testMovieExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(MovieRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    return Collections.singletonList(movie);
                });
        assertTrue(movieDao.movieExists(movie.getId()));
    }

    @Test
    public void testMovieExists_notExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(MovieRowMapper.class)))
                .thenReturn(Collections.emptyList());
        assertFalse(movieDao.movieExists(movie.getId()));
    }

    @Test
    public void testUserExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(BeanPropertyRowMapper.class)))
                .thenAnswer(invocation -> {
                    PreparedStatementSetter setter = invocation.getArgument(1);
                    PreparedStatement ps = mock(PreparedStatement.class);
                    setter.setValues(ps);
                    return Collections.singletonList(movie);
                });
        assertTrue(movieDao.userExists(userVote.getUserId()));
    }

    @Test
    public void testUserExists_notExists() {
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.emptyList());
        assertFalse(movieDao.userExists(userVote.getUserId()));
    }
}