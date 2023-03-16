package moviesApi.service.impl;

import moviesApi.domain.Review;
import moviesApi.repository.ReviewRepository;
import moviesApi.service.ReviewService;
import moviesApi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Returns a page of {@link Review} objects that belong to a specific movie ID.
     *
     * @param movieId  the ID of the movie to filter reviews by
     * @param pageable the pagination information
     * @return a list of {@link Review} objects filtered by the specified movie ID and sorted according to the provided sorting information in the {@code pageable} parameter
     * @throws IllegalArgumentException if {@code movieId} is null or invalid
     */
    @Override
    public List<Review> findByMovieId(Long movieId, Pageable pageable) {
        Stream<Review> reviewStream = reviewRepository.findByMovieId(movieId, pageable.getSort()).stream();

        return reviewStream
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
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
    public List<Review> findAll(Pageable pageable) {
        Stream<Review> reviewStream = reviewRepository.findAll(pageable.getSort()).stream();

        return reviewStream
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    /**
     * Validates the review based on the following criteria:
     * rating should be between {@link Constants#MIN_REVIEW_RATING} and {@link Constants#MAX_REVIEW_RATING}
     * review text should not be greater than {@link Constants#MAX_REVIEW_LENGTH} characters
     *
     * @param review the review to be validated
     * @throws IllegalArgumentException if the review rating or text does not meet the validation criteria
     */
    @Override
    public void validateReview(Review review) {
        if (review.getRating() < Constants.MIN_REVIEW_RATING || review.getRating() > Constants.MAX_REVIEW_RATING) {
            throw new IllegalArgumentException("Rating should be between " + Constants.MIN_REVIEW_RATING + " and " + Constants.MAX_REVIEW_RATING);
        }
        if (review.getText().length() > Constants.MAX_REVIEW_LENGTH) {
            throw new IllegalArgumentException("Review size can't be greater than " + Constants.MAX_REVIEW_LENGTH);
        }
    }

    @Transactional
    @Override
    public int deleteByMovieId(Long movieId) {
        return reviewRepository.deleteByMovieId(movieId);
    }
}
