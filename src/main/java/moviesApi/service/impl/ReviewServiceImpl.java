package moviesApi.service.impl;

import moviesApi.domain.Review;
import moviesApi.repository.ReviewRepository;
import moviesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void save(Review review) {
        reviewRepository.save(review);
    }

    @Override
    public List<Review> findReviewsByMovieId(Long movieId) {
        return reviewRepository.findReviewsByMovieId(movieId);
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return reviewRepository.findReviewById(reviewId);
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        reviewRepository.deleteReviewById(reviewId);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
}
