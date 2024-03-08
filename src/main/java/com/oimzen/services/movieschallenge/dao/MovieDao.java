package com.oimzen.services.movieschallenge.dao;

import com.oimzen.services.movieschallenge.dao.mapper.MovieRowMapper;
import com.oimzen.services.movieschallenge.dao.util.DaoConstants;
import com.oimzen.services.movieschallenge.model.Movie;
import com.oimzen.services.movieschallenge.model.UserFavorite;
import com.oimzen.services.movieschallenge.model.UserVote;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
//JdbcTemplate and it's connections are managed by spring, so we can consider it safe
@SuppressFBWarnings({"EI_EXPOSE_REP2", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE"})
public class MovieDao {

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(MovieDao.class);

    public List<Movie> getAllMovies() {
        logger.info("Querying db for all movies");
        final List<Movie> movies = jdbcTemplate.query(DaoConstants.SQL_QUERY_SELECT_ALL_MOVIES_ORDERED, new MovieRowMapper());
        logger.info("Found {} movies", movies.size());
        return movies;
    }

    public Optional<Movie> getMovieById(int id) {
        logger.info("Querying db for movie with id: {}", id);
        final String sql = DaoConstants.SQL_QUERY_SELECT_MOVIE_BY_ID;
        final List<Movie> movies = jdbcTemplate.query(sql, ps -> {
            ps.setInt(DaoConstants.PARAMETER_INDEX_ONE, id);
            logger.info("Attempting to execute SQL: {}", ps);
        }, new MovieRowMapper());
        if (movies.isEmpty()) {
            logger.info("No movie found with id: {}", id);
            return Optional.empty();
        }
        logger.info("Found movie with id: {}", id);
        return Optional.of(movies.getFirst());
    }

    public Movie addMovie(Movie movie) {

        logger.info("Attempting to insert into db movie: {}", movie);
        final String sql = DaoConstants.SQL_QUERY_INSERT_MOVIE;
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql, new String[]{DaoConstants.TABLE_MOVIES_FIELD_ID});
            ps.setInt(DaoConstants.PARAMETER_INDEX_ONE, movie.getDownvotes());
            ps.setString(DaoConstants.PARAMETER_INDEX_TWO, movie.getImageUrl());
            ps.setInt(DaoConstants.PARAMETER_INDEX_THREE, movie.getFavoriteCount());
            ps.setInt(DaoConstants.PARAMETER_INDEX_FOUR, movie.getScore());
            ps.setInt(DaoConstants.PARAMETER_INDEX_FIVE, movie.getReleaseYear());
            ps.setString(DaoConstants.PARAMETER_INDEX_SIX, movie.getTitle());
            ps.setInt(DaoConstants.PARAMETER_INDEX_SEVEN, movie.getUpvotes());
            ps.setString(DaoConstants.PARAMETER_INDEX_EIGHT, movie.getDetails());
            logger.info("Attempting to execute SQL: {}", ps);
            return ps;
        }, keyHolder);

        final Number key = keyHolder.getKey();
        if (Objects.isNull(key)) {
            logger.error("KeyHolder.getKey() returned null. When trying to insert movie: {}", movie);
            throw new RuntimeException("Failed to insert movie as no key was returned from the database.");
        }

        final int newMovieId = key.intValue();
        movie.setId(newMovieId);

        logger.info("Successfully inserted movie: {}", movie);

        return movie;
    }

    public Movie updateMovie(Movie movie) {
        logger.info("Attempting to update movie: {}", movie);
        final String sql = DaoConstants.SQL_QUERY_UPDATE_MOVIE;
        jdbcTemplate.update(sql,
                movie.getDownvotes(),
                movie.getImageUrl(),
                movie.getFavoriteCount(),
                movie.getScore(),
                movie.getReleaseYear(),
                movie.getTitle(),
                movie.getUpvotes(),
                movie.getDetails(),
                movie.getId()
        );
        logger.info("Successfully updated movie: {}", movie);
        return movie;
    }

    public void deleteMovie(int id) {
        logger.info("Attempting to delete movie with id: {}", id);
        jdbcTemplate.update(DaoConstants.SQL_QUERY_DELETE_MOVIE, id);
        logger.info("Successfully deleted movie with id: {}", id);
    }

    public Map<Integer, List<Movie>> getMoviesByYear() {
        logger.info("Querying db for all movies grouped by year");
        final List<Movie> movies = jdbcTemplate.query(DaoConstants.SQL_QUERY_SELECT_ALL_MOVIES, new MovieRowMapper());
        final Map<Integer, List<Movie>> moviesMap = movies.stream().collect(Collectors.groupingBy(Movie::getReleaseYear));
        logger.info("Found {} movies grouped in {} years", movies.size(), moviesMap.size());
        return moviesMap;
    }

    public List<Movie> getMoviesByYear(int year) {
        logger.info("Querying db for all movies with release year: {}", year);
        final String sql = DaoConstants.SQL_QUERY_SELECT_MOVIES_WITH_YEAR;
        final List<Movie> movies = jdbcTemplate.query(sql, ps -> {
            ps.setInt(DaoConstants.PARAMETER_INDEX_ONE, year);
            logger.info("Attempting to execute SQL: {}", ps);
        }, new MovieRowMapper());
        logger.info("Found {} movies with release year: {}", movies.size(), year);
        return movies;
    }

    public UserVote voteForMovie(UserVote userVote) {
        logger.info("Attempting to insert into db vote: {}", userVote);
        jdbcTemplate.update(DaoConstants.SQL_QUERY_INSERT_USERVOTE,
                userVote.getMovieId(),
                userVote.getUserId(),
                userVote.getVote()
        );
        logger.info("Successfully inserted vote: {}", userVote);
        return userVote;
    }

    public boolean voteExists(UserVote userVote) {
        logger.info("Checking if vote {} already exists", userVote);
        final String sql = DaoConstants.SQL_QUERY_SELECT_USERVOTE;
        final List<UserVote> votes = jdbcTemplate.query(sql, ps -> {
            ps.setInt(DaoConstants.PARAMETER_INDEX_ONE, userVote.getMovieId());
            ps.setInt(DaoConstants.PARAMETER_INDEX_TWO, userVote.getUserId());
            logger.info("Attempting to execute SQL: {}", ps);
        }, new BeanPropertyRowMapper<>(UserVote.class));
        final boolean result = !votes.isEmpty(); //If the list is not empty, the vote exists
        logger.info( result ? "Vote {} already exists" : "Vote {} does not exist", userVote);
        return result;
    }

    public UserVote updateVoteForMovie(UserVote userVote) {
        logger.info("Attempting to update vote: {}", userVote);
        jdbcTemplate.update(DaoConstants.SQL_QUERY_UPDATE_USERVOTE,
                userVote.getVote(),
                userVote.getMovieId(),
                userVote.getUserId()
        );
        logger.info("Successfully updated vote: {}", userVote);
        return userVote;
    }

    public void deleteVote(UserVote userVote) {
        logger.info("Attempting to delete vote: {}", userVote);
        jdbcTemplate.update(DaoConstants.SQL_QUERY_DELETE_USERVOTE,
                userVote.getMovieId(),
                userVote.getUserId()
        );
        logger.info("Successfully deleted vote: {}", userVote);
    }

    public UserFavorite favoriteMovie(UserFavorite userFavorite) {
        logger.info("Attempting to insert into db favorite: {}", userFavorite);
        jdbcTemplate.update(DaoConstants.SQL_QUERY_INSERT_USERFAVORITE,
                userFavorite.getMovieId(),
                userFavorite.getUserId()
        );
        logger.info("Successfully inserted favorite: {}", userFavorite);
        return userFavorite;
    }

    public boolean favoriteExists(UserFavorite userFavorite) {
        logger.info("Checking if favorite {} already exists", userFavorite);
        final String sql = DaoConstants.SQL_QUERY_SELECT_USERFAVORITE;
        final List<UserFavorite> favorites = jdbcTemplate.query(sql, ps -> {
            ps.setInt(DaoConstants.PARAMETER_INDEX_ONE, userFavorite.getMovieId());
            ps.setInt(DaoConstants.PARAMETER_INDEX_TWO, userFavorite.getUserId());
            logger.info("Attempting to execute SQL: {}", ps);
        }, new BeanPropertyRowMapper<>(UserFavorite.class));
        final boolean result = !favorites.isEmpty(); //If the list is not empty, the favorite exists
        logger.info(result ? "Favorite {} already exists" : "Favorite {} does not exist", userFavorite);
        return result;
    }
    public void deleteFavorite(UserFavorite userFavorite) {
        logger.info("Attempting to delete favorite: {}", userFavorite);
        jdbcTemplate.update(DaoConstants.SQL_QUERY_DELETE_USERFAVORITE,
                userFavorite.getMovieId(),
                userFavorite.getUserId()
        );
        logger.info("Successfully deleted favorite: {}", userFavorite);
    }

    public boolean movieExists(int movieId) {
        logger.info("Checking if movie with id: {} exists", movieId);
        final Optional<Movie> movie = getMovieById(movieId);
        final boolean result = movie.isPresent(); //If the movie is present, it exists
        logger.info(result ? "Movie with id: {} exists" : "Movie with id: {} does not exist", movieId);
        return result;
    }

    public boolean userExists(int userId) {
        logger.info("Checking if user with id: {} exists", userId);
        final String sql = DaoConstants.SQL_QUERY_SELECT_USER;
        final List<UserFavorite> users =
                jdbcTemplate.query(sql, ps -> {
                    ps.setInt(DaoConstants.PARAMETER_INDEX_ONE, userId);
                    logger.info("Attempting to execute SQL: {}", ps);
                }, new BeanPropertyRowMapper<>(UserFavorite.class));
        final boolean result = !users.isEmpty(); //If the list is not empty, the user exists
        logger.info(result ? "User with id: {} exists" : "User with id: {} does not exist", userId);
        return result;
    }
}
