package moviesApi.filter;

import moviesApi.domain.Review;

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
            this.text = text;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withRating(Float rating) {
            this.rating = rating;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withMovieId(Long movieId) {
            this.movieId = movieId;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withId(Long id) {
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
