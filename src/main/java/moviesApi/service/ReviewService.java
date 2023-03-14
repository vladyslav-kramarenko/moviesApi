package moviesApi.service;

import moviesApi.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review save(Review review);

    List<Review> findByMovieId(Long movieId);

    Optional<Review> findById(Long reviewId);

    void deleteById(Long reviewId);

    List<Review> findAll();

    void validateReview(Review review);

    int deleteByMovieId(Long movieId);
}
