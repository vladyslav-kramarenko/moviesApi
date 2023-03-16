package moviesApi.service;

import moviesApi.domain.Review;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review save(Review review);

    List<Review> findByMovieId(Long movieId, Pageable pageable);

    Optional<Review> findById(Long reviewId);

    void deleteById(Long reviewId);

    List<Review> findAll();

    void validateReview(Review review);

    int deleteByMovieId(Long movieId);
}
