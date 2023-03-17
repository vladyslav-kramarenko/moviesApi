package moviesApi.filter;

import moviesApi.domain.Review;
import moviesApi.util.Constants;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class ReviewFilter {

    private String text;
    private Float rating;
    private LocalDateTime dateTime;
    private Long movieId;
    private Long id;

    public String getText() {
        return text;
    }

    public Float getRating() {
        return rating;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Long getMovieId() {
        return movieId;
    }

    public Long getId() {
        return id;
    }

    public static ReviewFilter.ReviewFilterBuilder builder() {
        return new ReviewFilter.ReviewFilterBuilder();
    }

    public static class ReviewFilterBuilder {
        private String text;
        private Float rating;
        private LocalDateTime dateTime;
        private Long movieId;
        private Long id;


        public ReviewFilter.ReviewFilterBuilder withText(String text) {
            if (text != null && text.length() > Constants.MAX_REVIEW_LENGTH) {
                throw new IllegalArgumentException("Review text must be under " + Constants.MAX_REVIEW_LENGTH + " characters");
            }
            this.text = text;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withRating(Float rating) {
            if (rating != null && (rating < Constants.MIN_REVIEW_RATING || rating > Constants.MAX_REVIEW_RATING)) {
                throw new IllegalArgumentException("rating must be between " + Constants.MIN_REVIEW_RATING + " and " + Constants.MAX_REVIEW_RATING);
            }
            this.rating = rating;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withDateTime(LocalDateTime dateTime) {
            if (dateTime != null && dateTime.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("dateTime must be a past or present date/time");
            }
            this.dateTime = dateTime;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withMovieId(Long movieId) {
            if (id != null && movieId < 0) {
                throw new IllegalArgumentException("movieId should be positive");
            }
            this.movieId = movieId;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withId(Long id) {
            if (id != null && id < 0) {
                throw new IllegalArgumentException("ID should be positive");
            }
            this.id = id;
            return this;
        }

        public ReviewFilter build() {
            return new ReviewFilter(this);
        }
    }

    private ReviewFilter(ReviewFilter.ReviewFilterBuilder builder) {
        this.text = builder.text;
        this.id = builder.id;
        this.dateTime = builder.dateTime;
        this.movieId = builder.movieId;
        this.rating = builder.rating;
    }

    public Stream<Review> filter(Stream<Review> input) {
        return input
                .filter(review -> (text == null || review.getText().toLowerCase().contains(text.toLowerCase())))
                .filter(review -> (id == null || review.getId().equals(id)))
                .filter(review -> (movieId == null || review.getMovieId().equals(movieId)))
                .filter(review -> (rating == null || review.getRating().equals(rating)))
                .filter(review -> (dateTime == null || review.getDateTime().equals(dateTime)));
    }
}
