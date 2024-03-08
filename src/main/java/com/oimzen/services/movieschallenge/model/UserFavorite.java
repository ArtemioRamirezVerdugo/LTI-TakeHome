package com.oimzen.services.movieschallenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class UserFavorite {

    private int userId;
    private int movieId;
}
