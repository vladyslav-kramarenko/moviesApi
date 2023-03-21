package moviesApi.controller;

import moviesApi.SecurityConfig;
import moviesApi.domain.Review;
import moviesApi.service.ReviewService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static moviesApi.util.ControllerHelp.generateReview;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "moviesApi")
@Import(SecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReviewControllerTest {
    @Autowired
    private ReviewController reviewController;


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteReviewById() {
        Long wrongId = -1L;
        // Call the deleteReviewById method with wrong id
        ResponseEntity<?> response = reviewController.deleteReview(wrongId);
        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Create a test review
        Review testReview = generateReview();

        entityManager.persist(testReview);
        entityManager.flush();

        // Call the deleteReviewById method
        response = reviewController.deleteReview(testReview.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Check that the review has been deleted
        Optional<Review> reviewOptional = reviewService.findById(testReview.getId());
        assertFalse(reviewOptional.isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllReviews() {
        // Create test reviews
        Review testReview1 = generateReview();

        entityManager.persist(testReview1);
        entityManager.flush();

        // test getAllReviews method with wrong movie id
        long wrongMovieId = -1L;
        ResponseEntity<?> response = reviewController.getAllReviews(null, wrongMovieId, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // test getAllReviews method with not existing movie id
        wrongMovieId = 9999999L;
        response = reviewController.getAllReviews(null, wrongMovieId, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());


        // test getAllReviews method
        response = reviewController.getAllReviews(null, null, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Review> allReviews = (List<Review>) response.getBody();
        assertNotNull(allReviews);
        assertTrue(allReviews.contains(testReview1));

        // Delete the test reviews
        reviewService.deleteById(testReview1.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateReviewById() {
        // Create a test review
        Review testReview = generateReview();

        entityManager.persist(testReview);
        entityManager.flush();

        // Update the test review
        Review updatedReview = new Review();
        updatedReview.setRating(4f);
        updatedReview.setText("Updated review text");

        // Call the updateReviewById method with wrong id
        Long wrongId = -1L;
        ResponseEntity<Review> response = reviewController.updateReview(wrongId, updatedReview);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Call the updateReviewById method
        response = reviewController.updateReview(testReview.getId(), updatedReview);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReview.getRating(), response.getBody().getRating());
        assertEquals(updatedReview.getText(), response.getBody().getText());

        // Delete the test review
        reviewService.deleteById(testReview.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getReviewsById() {
        Long wrongId = -1L;
        //test with wrong id
        ResponseEntity<Review> response = reviewController.getReviewById(wrongId);
        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Create a test review
        Review testReview = generateReview();
        reviewService.save(testReview.getMovieId(), testReview);

        response = reviewController.getReviewById(testReview.getId());
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReview, response.getBody());

        // Clean up test data
        reviewService.deleteById(testReview.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetCount() {
        // Create test reviews
        Review testReview1 = generateReview();
        testReview1.setText("test text for test");

        entityManager.persist(testReview1);
        entityManager.flush();

        // test getCount method with wrong movie id
        ResponseEntity<?> response = reviewController.getCount(LocalDateTime.of(3000, 10, 10, 10, 10), null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // test getCount method with wrong movie id
        Long wrongMovieId = -1L;
        response = reviewController.getCount(null, wrongMovieId, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // test getAllReviews method
        response = reviewController.getCount(null, null, null, testReview1.getText());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody());

        // Delete the test reviews
        reviewService.deleteById(testReview1.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetCountInvalidParameter() throws Exception {
        // Invalid parameter value: rating should be between 0 and 10
        float invalidRating = 11;

        // Build request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("rating", Float.toString(invalidRating));
        params.add("movieId", "1");

        // Perform GET request and expect a bad request response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/count").params(params))
                .andExpect(status().isBadRequest());
    }
}