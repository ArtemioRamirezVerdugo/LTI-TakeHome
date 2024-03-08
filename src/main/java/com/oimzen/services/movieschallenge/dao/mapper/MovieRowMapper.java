package com.oimzen.services.movieschallenge.dao.mapper;

import com.oimzen.services.movieschallenge.model.Movie;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieRowMapper implements RowMapper<Movie> {
        @Override
        public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Movie movie = new Movie();
            movie.setId(rs.getInt("id"));
            movie.setDownvotes(rs.getInt("downvotes"));
            movie.setImageUrl(rs.getString("image_url"));
            movie.setFavoriteCount(rs.getInt("favorite_count"));
            movie.setReleaseYear(rs.getInt("release_year"));
            movie.setScore(rs.getInt("score"));
            movie.setTitle(rs.getString("title"));
            movie.setUpvotes(rs.getInt("upvotes"));
            movie.setDetails(rs.getString("details"));
            return movie;
        }

}
