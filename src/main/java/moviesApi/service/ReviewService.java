package moviesApi.service;

import moviesApi.domain.Review;
import moviesApi.filter.ReviewFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewService {

    Review save(Review review);

    Optional<Review> findById(Long reviewId);

    void deleteById(Long reviewId);

    List<Review> findAll(ReviewFilter reviewFilter, Pageable pageable);

    long count(ReviewFilter reviewFilter);

    void validateReview(Review review);

    int deleteByMovieId(Long movieId);

    List<Map<String, Long>>  getMovieCountByRating();
}
