package moviesApi.filter;

import moviesApi.domain.Review;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static moviesApi.util.TestHelper.generateReviewWithParams;
import static org.junit.Assert.assertEquals;

public class ReviewFilterTest {
    @Test
    public void testFilter() {
        // Create some review objects
        Review r1 = generateReviewWithParams(1L,4.5f,"Great movie!",LocalDateTime.now());
        Review r2 =generateReviewWithParams(1L,1.5f,"Terrible movie :(",LocalDateTime.now().minusDays(1));
        Review r3 =generateReviewWithParams(2L,3.0f,"Average movie",LocalDateTime.now().minusDays(2));

        List<Review> reviews = new ArrayList<>();
        reviews.add(r1);
        reviews.add(r2);
        reviews.add(r3);

        // Create a ReviewFilter object to filter the reviews
        ReviewFilter filter = ReviewFilter.builder()
                .withText("movie")
                .withRatingFrom(3.0f)
                .withMovieId(1L)
                .withFromDateTime(LocalDateTime.now().minusDays(1))
                .build();

        // Filter the reviews using the ReviewFilter object
        Stream<Review> filteredReviews = filter.filter(reviews.stream());

        // Verify that the filtered reviews match the expected results
        List<Review> expectedReviews = new ArrayList<>();
        expectedReviews.add(r1);

        assertEquals(expectedReviews, filteredReviews.toList());
    }
}
