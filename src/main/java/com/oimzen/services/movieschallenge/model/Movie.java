package com.oimzen.services.movieschallenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Movie {

    private int id;
    //Downvotes can easily be kept up to date by using a trigger in the database
    private int downvotes;
    private String imageUrl;
    //Favorite count can easily be kept up to date by using a trigger in the database
    private int favoriteCount;
    private int releaseYear;
    //Score can easily be kept up to date by using a trigger in the database
    private int score;
    private String title;
    //Upvotes can easily be kept up to date by using a trigger in the database
    private int upvotes;
    /*  In reality details would include more fields like:
        genre, duration, director, actors, awards, etc.
        However, for the purpose of this demo, I will keep it simple. */
    private String details;

}
