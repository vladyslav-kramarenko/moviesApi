package moviesApi.service;

import moviesApi.domain.Review;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review save(Review review);

    Optional<Review> findById(Long reviewId);

    void deleteById(Long reviewId);

    List<Review> findAll(Long movieId, Float rating, Pageable pageable);

    void validateReview(Review review);

    int deleteByMovieId(Long movieId);
}
