package com.bongachat;

public class Movie {
    private String imageUrl;
    private String movieTitle;
    private int rank;

    public Movie(String imageUrl, String movieTitle, int rank) {
        this.imageUrl = imageUrl;
        this.movieTitle = movieTitle;
        this.rank = rank;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public int getRank() {
        return rank;
    }
}
