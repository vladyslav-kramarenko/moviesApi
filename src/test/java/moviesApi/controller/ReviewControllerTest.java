package moviesApi.controller;

import moviesApi.domain.Review;
import moviesApi.service.ReviewService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
//@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3307/movies",
        "spring.datasource.username=user",
        "spring.datasource.password=app_password"
})
@ComponentScan(basePackages = "moviesApi")
class ReviewControllerTest {

    @Autowired
    private ReviewController reviewController;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testDeleteReviewById() {
        // Create a test review
        Review testReview = generateReview();

        entityManager.persist(testReview);
        entityManager.flush();

        // Call the deleteReviewById method
        ResponseEntity<?> response = reviewController.deleteReview(testReview.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Check that the review has been deleted
        Optional<Review> reviewOptional = reviewService.findById(testReview.getId());
        assertFalse(reviewOptional.isPresent());
    }

    @Test
    public void testGetAllReviews() {
        // Create test reviews
        Review testReview1 = generateReview();

        entityManager.persist(testReview1);
        entityManager.flush();

        // Call the getAllReviews method
        ResponseEntity<List<Review>> response = reviewController.getReviewsByMovieId();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Review> allReviews=response.getBody();
        assertNotNull(allReviews);
        assertTrue(allReviews.contains(testReview1));

        // Delete the test reviews
        reviewService.deleteById(testReview1.getId());
    }

    @Test
    public void testUpdateReviewById() {
        // Create a test review
        Review testReview = generateReview();

        entityManager.persist(testReview);
        entityManager.flush();

        // Update the test review
        Review updatedReview = new Review();
        updatedReview.setRating(4);
        updatedReview.setText("Updated review text");

        // Call the updateReviewById method
        ResponseEntity<Review> response = reviewController.updateReview(testReview.getId(), updatedReview);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReview.getRating(), response.getBody().getRating());
        assertEquals(updatedReview.getText(), response.getBody().getText());

        // Delete the test review
        reviewService.deleteById(testReview.getId());
    }

    private static Review generateReview() {
        Review review = new Review();
        review.setRating(5);
        review.setText("Test review text");
        review.setMovieId(2L);
        review.setDateTime(LocalDateTime.now());
        return review;
    }
}