package com.williamito.popularmovies;

/**
 * Holds the needed info about a movie for phase 1 of project
 */
public class Movie {

    private String id = "", title = "", releaseDate = "", posterPath = "", voteAverage = "", overview = "";

    public Movie(String id,
                 String title,
                 String releaseDate,
                 String posterPath,
                 String voteAverage,
                 String overview) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s", this.id, this.title, this.releaseDate, this.posterPath, this.voteAverage, this.overview);
    }
}
