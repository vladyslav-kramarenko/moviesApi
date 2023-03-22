package moviesApi.filter;

import moviesApi.domain.Review;
import moviesApi.util.Constants;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class ReviewFilter {

    private String text;
    private Float rating;
    private Float ratingFrom;
    private Float ratingTo;
    private LocalDateTime dateTime;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private Long movieId;

    public Float getRatingFrom() {
        return ratingFrom;
    }

    public Float getRatingTo() {
        return ratingTo;
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

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


    public static ReviewFilter.ReviewFilterBuilder builder() {
        return new ReviewFilter.ReviewFilterBuilder();
    }

    public static class ReviewFilterBuilder {
        private String text;
        private Float rating;
        private Float ratingFrom;
        private Float ratingTo;
        private LocalDateTime dateTime;
        private LocalDateTime fromDateTime;
        private LocalDateTime toDateTime;
        private Long movieId;


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

        public ReviewFilter.ReviewFilterBuilder withRatingTo(Float ratingTo) {
            if (ratingTo != null && (ratingTo < Constants.MIN_REVIEW_RATING)) {
                throw new IllegalArgumentException("rating must be greater than " + Constants.MIN_REVIEW_RATING);
            }
            this.ratingTo = ratingTo;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withRatingFrom(Float ratingFrom) {
            if (ratingFrom != null && (ratingFrom > Constants.MAX_REVIEW_RATING)) {
                throw new IllegalArgumentException("rating must be smaller than " + Constants.MAX_REVIEW_RATING);
            }
            this.ratingFrom = ratingFrom;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withDateTime(LocalDateTime dateTime) {
            if (dateTime != null && dateTime.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("dateTime must be a past or present date/time");
            }
            this.dateTime = dateTime;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withFromDateTime(LocalDateTime fromDateTime) {
            if (fromDateTime != null && fromDateTime.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Filter from date must be a past or present date");
            }
            this.fromDateTime = fromDateTime;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withToDateTime(LocalDateTime toDateTime) {
            this.toDateTime = toDateTime;
            return this;
        }

        public ReviewFilter.ReviewFilterBuilder withMovieId(Long movieId) {
            if (movieId != null && movieId < 0) {
                throw new IllegalArgumentException("movieId should be positive");
            }
            this.movieId = movieId;
            return this;
        }

        public ReviewFilter build() {
            return new ReviewFilter(this);
        }
    }

    private ReviewFilter(ReviewFilter.ReviewFilterBuilder builder) {
        this.text = builder.text;
        this.fromDateTime = builder.fromDateTime;
        this.toDateTime = builder.toDateTime;
        this.dateTime = builder.dateTime;
        this.movieId = builder.movieId;
        this.rating = builder.rating;
        this.ratingTo = builder.ratingTo;
        this.ratingFrom = builder.ratingFrom;
    }

    public Stream<Review> filter(Stream<Review> input) {
        return input
                .filter(review -> (text == null || review.getText().toLowerCase().contains(text.toLowerCase())))
                .filter(review -> (movieId == null || review.getMovieId().equals(movieId)))
                .filter(review -> (rating == null || review.getRating().equals(rating)))
                .filter(review -> (ratingTo == null || review.getRating() <= ratingTo))
                .filter(review -> (ratingFrom == null || review.getRating() >= ratingFrom))
                .filter(review -> (dateTime == null || review.getDateTime().equals(dateTime)));
    }
}
