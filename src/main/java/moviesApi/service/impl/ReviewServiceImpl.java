package moviesApi.service.impl;

import moviesApi.domain.Review;
import moviesApi.repository.ReviewRepository;
import moviesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> findByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public void validateReview(Review review) {
        int minRating = 1;
        int maxRating = 10;
        if (review.getRating() < minRating || review.getRating() > maxRating) {
            throw new IllegalArgumentException("Rating should be between 1.0 and 10.0");
        }
    }


    @Transactional
    @Override
    public int deleteByMovieId(Long movieId) {
        return reviewRepository.deleteByMovieId(movieId);
    }
}
