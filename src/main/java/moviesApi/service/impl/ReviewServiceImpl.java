package moviesApi.service.impl;

import moviesApi.domain.Review;
import moviesApi.filter.ReviewFilter;
import moviesApi.repository.ReviewRepository;
import moviesApi.service.ReviewService;
import moviesApi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static moviesApi.util.Utilities.mapsToListOfSingletonMaps;

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
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> findAll(ReviewFilter reviewFilter, Pageable pageable) {
        Stream<Review> reviewStream = reviewRepository.findAll(pageable.getSort()).stream();
        return reviewFilter.filter(reviewStream)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    @Override
    public long count(ReviewFilter reviewFilter) {
        Stream<Review> reviewStream = reviewRepository.findAll().stream();
        return reviewFilter.filter(reviewStream).count();
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

    @Override
    public List<Map<String, Long>> getMovieCountByRating() {
        List<Float[]> ratingGroupLimits = Arrays.asList(
                new Float[]{1f, 4.9f},
                new Float[]{5f, 7.9f},
                new Float[]{8f, 10f});
        return getMovieCountByRating(ratingGroupLimits);
    }

    /**
     * Returns a list of maps, where each map contains a singleton map where the key is a rating group
     * and the value is the count of movies with the average rating within this rating group.
     * The rating groups are determined by the lower bounds of the rating intervals passed as input.
     *
     * @param ratingGroupLimits a list of float arrays where each float array represents a rating interval
     * @return a list of maps, where each map contains a singleton map with the rating group and the count of movies
     */
    public List<Map<String, Long>> getMovieCountByRating(List<Float[]> ratingGroupLimits) {
        try {
            Map<String, Long> ratingCounts = createRatingGroups(ratingGroupLimits);
            Map<Long, List<Float>> movieRatings = getMoviesRatings(reviewRepository.findAll());

            for (Map.Entry<Long, List<Float>> entry : movieRatings.entrySet()) {

                double averageRating = entry.getValue().stream()
                        .mapToDouble(Float::doubleValue)
                        .average()
                        .orElse(0.0);
                String ratingGroup = getRatingGroup(averageRating, ratingGroupLimits);

                ratingCounts.putIfAbsent(ratingGroup, 0L);
                ratingCounts.compute(ratingGroup, (k, v) -> v + 1);
            }

            return mapsToListOfSingletonMaps(ratingCounts);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Groups the reviews by movie ID and returns a map where each key is a movie ID and each value is a list of ratings for that movie
     *
     * @param reviews the list of reviews to be grouped
     * @return a map where each key is a movie ID and each value is a list of ratings for that movie
     */
    private static Map<Long, List<Float>> getMoviesRatings(List<Review> reviews) {
        return reviews
                .stream()
                .collect(Collectors.groupingBy(Review::getMovieId,
                        Collectors.mapping(Review::getRating, Collectors.toList())));
    }

    /**
     * Creates a map of rating groups with initial counts set to 0
     *
     * @param ratingGroupLimits a list of float arrays where each float array represents a rating interval
     * @return a map of rating groups with initial counts set to 0
     */
    public static Map<String, Long> createRatingGroups(List<Float[]> ratingGroupLimits) {
        Map<String, Long> ratingCounts = new HashMap<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        for (Float[] interval : ratingGroupLimits) {
            String lowerBound = decimalFormat.format(interval[0]);
            String upperBound = decimalFormat.format(interval[1]);
            ratingCounts.put(lowerBound + "-" + upperBound, 0L);
        }
        return ratingCounts;
    }

    /**
     * Returns the rating group to which the average rating belongs according to the given rating intervals.
     * The rating group limits are represented as a list of float arrays where each array contains the lower and upper bounds
     * of a rating interval. For example, if the rating group limits are [[1.0, 4.9], [5.0, 7.9], [8.0, 10.0]],
     * a movie with an average rating of 3.5 will belong to the "1.0-4.9" rating group, while a movie with an average rating
     * of 7.9 will belong to the "5.0-7.9" rating group.
     *
     * @param averageRating     the average rating of a movie
     * @param ratingGroupLimits a list of float arrays representing the lower and upper bounds of rating intervals
     * @return the rating group to which the average rating belongs, in the format of a string containing the lower and upper bounds of the rating interval
     */
    public static String getRatingGroup(double averageRating, List<Float[]> ratingGroupLimits) {
        for (Float[] limit : ratingGroupLimits) {
            if (averageRating >= limit[0] && averageRating <= limit[1]) {
                return String.format("%.1f-%.1f", limit[0], limit[1]);
            }
        }
        return "";
    }
}
