package com.oimzen.services.movieschallenge.dao.mapper;

import com.oimzen.services.movieschallenge.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class MovieRowMapperTest {

    @Mock
    private ResultSet resultSet;

    private MovieRowMapper movieRowMapper;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        movieRowMapper = new MovieRowMapper();

        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("downvotes")).thenReturn(2);
        when(resultSet.getString("image_url")).thenReturn("test_image_url");
        when(resultSet.getInt("favorite_count")).thenReturn(3);
        when(resultSet.getInt("release_year")).thenReturn(2000);
        when(resultSet.getInt("score")).thenReturn(4);
        when(resultSet.getString("title")).thenReturn("test_title");
        when(resultSet.getInt("upvotes")).thenReturn(5);
        when(resultSet.getString("details")).thenReturn("test_details");
    }

    @Test
    public void mapRow() throws SQLException {
        Movie movie = movieRowMapper.mapRow(resultSet, 1);

        assertNotNull(movie, "Movie should not be null");
        assertEquals(1, movie.getId());
        assertEquals(2, movie.getDownvotes());
        assertEquals("test_image_url", movie.getImageUrl());
        assertEquals(3, movie.getFavoriteCount());
        assertEquals(2000, movie.getReleaseYear());
        assertEquals(4, movie.getScore());
        assertEquals("test_title", movie.getTitle());
        assertEquals(5, movie.getUpvotes());
        assertEquals("test_details", movie.getDetails());
    }
}