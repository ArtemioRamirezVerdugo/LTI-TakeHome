package com.oimzen.services.movieschallenge.service;

import com.oimzen.services.movieschallenge.dao.MovieDao;
import com.oimzen.services.movieschallenge.exception.MovieNotFoundException;
import com.oimzen.services.movieschallenge.exception.UserNotFoundException;
import com.oimzen.services.movieschallenge.model.Movie;
import com.oimzen.services.movieschallenge.model.UserFavorite;
import com.oimzen.services.movieschallenge.model.UserVote;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP") //We own MovieDao implementation, so we can consider it safe
public class MovieService {

    private final MovieDao movieDao;

    private final Logger logger = LoggerFactory.getLogger(MovieService.class);
    public List<Movie> getAllMovies() {
        return movieDao.getAllMovies();
    }

    public Optional<Movie> getMovieById(int id) {
        return movieDao.getMovieById(id);
    }

    public Movie addMovie(Movie movie) {
        return movieDao.addMovie(movie);
    }

    public Movie updateMovie(Movie movie) {
        return movieDao.updateMovie(movie);
    }

    public void deleteMovie(int id) {
        movieDao.deleteMovie(id);
    }

    public Map<Integer, List<Movie>> getMoviesByYear() {
        return movieDao.getMoviesByYear();
    }

    public List<Movie> getMoviesByYear(int year) {
        return movieDao.getMoviesByYear(year);
    }

    public UserVote voteForMovie(UserVote userVote) {
        if (!movieDao.movieExists(userVote.getMovieId())) {
            throw new MovieNotFoundException(userVote.getMovieId());
        }
        if (!movieDao.userExists(userVote.getUserId())) {
            throw new UserNotFoundException(userVote.getUserId());
        }
        logger.info("Checking if vote {} already exists", userVote);
        if (movieDao.voteExists(userVote)) {
            logger.info("Vote {} already exists, updating existing vote", userVote);
            return movieDao.updateVoteForMovie(userVote);
        }
        logger.info("Vote {} does not exist, creating new vote", userVote);
        return movieDao.voteForMovie(userVote);
    }

    public void deleteVote(UserVote userVote) {
        movieDao.deleteVote(userVote);
    }

    public UserFavorite favoriteMovie(UserFavorite userFavorite) {
        if (!movieDao.movieExists(userFavorite.getMovieId())) {
            throw new MovieNotFoundException(userFavorite.getMovieId());
        }
        if (!movieDao.userExists(userFavorite.getUserId())) {
            throw new UserNotFoundException(userFavorite.getUserId());
        }
        logger.info("Checking if favorite {} already exists", userFavorite);
        if (movieDao.favoriteExists(userFavorite)) {
            logger.info("Favorite {} already exists no further action is needed", userFavorite);
            return userFavorite;
        }
        logger.info("Favorite {} does not exist, creating new favorite", userFavorite);
        return movieDao.favoriteMovie(userFavorite);
    }

    public void deleteFavorite(UserFavorite userFavorite) {
        movieDao.deleteFavorite(userFavorite);
    }

}
