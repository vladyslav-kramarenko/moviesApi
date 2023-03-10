package moviesApi.domain;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    private float rating;
    private LocalDateTime dateTime;
    private int movieId;

    public Review(int id, String text, float rating, LocalDateTime dateTime, int movieId) {
        this.id = id;
        this.text = text;
        this.rating = rating;
        this.dateTime = dateTime;
        this.movieId = movieId;
    }
}