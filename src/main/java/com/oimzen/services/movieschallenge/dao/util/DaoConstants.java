package com.oimzen.services.movieschallenge.dao.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DaoConstants {

    public static final String SQL_QUERY_SELECT_ALL_MOVIES = "SELECT * FROM movies";
    public static final String SQL_QUERY_SELECT_ALL_MOVIES_ORDERED = "SELECT * FROM movies ORDER BY favorite_count DESC";
    public static final String SQL_QUERY_SELECT_MOVIE_BY_ID = "SELECT * FROM movies WHERE id = ?";
    public static final String SQL_QUERY_SELECT_MOVIES_WITH_YEAR = "SELECT * FROM movies where release_year = ?";
    public static final String SQL_QUERY_INSERT_MOVIE =
            "INSERT INTO movies (downvotes, image_url, favorite_count, score, release_year, title, upvotes, details)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_QUERY_UPDATE_MOVIE =
            "UPDATE movies SET downvotes = ?, image_url = ?, favorite_count = ?, score = ?, release_year = ?, title = ?, upvotes = ?,"
                    + "details = ? WHERE id = ?";
    public static final String SQL_QUERY_DELETE_MOVIE = "DELETE FROM movies WHERE id = ?";

    public static final String SQL_QUERY_INSERT_USERVOTE = "INSERT INTO user_votes (movie_id, user_id, vote) VALUES (?, ?, ?)";
    public static final String SQL_QUERY_SELECT_USERVOTE = "SELECT * FROM user_votes WHERE movie_id = ? AND user_id = ?";
    public static final String SQL_QUERY_UPDATE_USERVOTE = "UPDATE user_votes SET vote = ? WHERE movie_id = ? AND user_id = ?";
    public static final String SQL_QUERY_DELETE_USERVOTE = "DELETE FROM user_votes WHERE movie_id = ? AND user_id = ?";

    public static final String SQL_QUERY_INSERT_USERFAVORITE = "INSERT INTO user_favorites (movie_id, user_id) VALUES (?, ?)";
    public static final String SQL_QUERY_SELECT_USERFAVORITE = "SELECT * FROM user_favorites where movie_id = ? AND user_id = ?";
    public static final String SQL_QUERY_DELETE_USERFAVORITE = "DELETE FROM user_favorites WHERE movie_id = ? AND user_id = ?";

    public static final String SQL_QUERY_SELECT_USER = "SELECT * FROM users WHERE id = ?";

    public static final String TABLE_MOVIES_FIELD_ID = "id";

    public static final int PARAMETER_INDEX_ONE = 1;
    public static final int PARAMETER_INDEX_TWO = 2;
    public static final int PARAMETER_INDEX_THREE = 3;
    public static final int PARAMETER_INDEX_FOUR = 4;
    public static final int PARAMETER_INDEX_FIVE = 5;
    public static final int PARAMETER_INDEX_SIX = 6;
    public static final int PARAMETER_INDEX_SEVEN = 7;
    public static final int PARAMETER_INDEX_EIGHT = 8;

}
