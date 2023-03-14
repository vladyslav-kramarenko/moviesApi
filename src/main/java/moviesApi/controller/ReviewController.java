package moviesApi.controller;

import moviesApi.domain.Review;
import moviesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // DELETE a review by ID
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);
        if (reviewOptional.isPresent()) {
            reviewService.deleteReviewById(reviewId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET all reviews
    @GetMapping("")
    public ResponseEntity<List<Review>> getReviewsByMovieId() {
        List<Review> reviews = reviewService.findAll();
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(reviews);
        }
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "This is a test endpoint";
    }
}
