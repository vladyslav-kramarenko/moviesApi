package moviesApi.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import moviesApi.util.Constants;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Min(value = Constants.MIN_REVIEW_RATING, message = "rating must be between " + Constants.MIN_REVIEW_RATING + " and " + Constants.MAX_REVIEW_RATING)
    @Max(value = Constants.MAX_REVIEW_RATING, message = "rating must be between " + Constants.MIN_REVIEW_RATING + " and " + Constants.MAX_REVIEW_RATING)
    private Float rating;
    private LocalDateTime dateTime;
    @NotNull
    @Positive
    private Long movieId;
    @Size(max = Constants.MAX_REVIEW_LENGTH, message = "Review text must be under " + Constants.MAX_REVIEW_LENGTH + " characters")
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}