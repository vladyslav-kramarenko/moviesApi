package moviesApi.dto;

import moviesApi.domain.Review;

import java.time.LocalDateTime;

public class ReviewRecord {
    private Long id;
    private Float rating;
    private LocalDateTime dateTime;
    private MovieRecord movie;
    private String text;

    public ReviewRecord() {
    }

    public ReviewRecord(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.dateTime = review.getDateTime();
        this.text = review.getText();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public MovieRecord getMovie() {
        return movie;
    }

    public void setMovie(MovieRecord movie) {
        this.movie = movie;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
