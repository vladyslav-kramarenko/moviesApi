package moviesApi.service;

import moviesApi.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    void save(Review review);

    List<Review> findReviewsByMovieId(Long movieId);

    Optional<Review> findReviewById(Long reviewId);

    void deleteReviewById(Long reviewId);

    List<Review> findAll();
}
