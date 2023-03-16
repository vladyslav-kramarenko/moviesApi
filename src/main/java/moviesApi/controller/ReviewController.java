package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import moviesApi.domain.Review;
import moviesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static moviesApi.util.Utilities.createSort;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller", description = "APIs for managing reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "The review you are trying to delete doesn't exist")
    })
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        Optional<Review> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isPresent()) {
            reviewService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    @Operation(summary = "View a list of all available reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of reviews"),
            @ApiResponse(responseCode = "404", description = "No reviews found")
    })
    @Parameters({
            @Parameter(name = "movie_id", description = "Filter reviews by movie ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "rating", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "date_time", description = "Filter reviews by date/time", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(
                    name = "sort",
                    description = "Sort reviews by property and order (allowed properties: id, movieId,rating, dateTime; allowed order types: asc, desc)",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "dateTime,asc"
            ))
    })
    public ResponseEntity<List<Review>> getAllReviews(
//            @RequestParam(name = "date_time", required = false) String dateTime,
            @RequestParam(name = "movie_id", required = false) Long movieId,
            @RequestParam(name = "rating", required = false) Float rating,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "dateTime,asc") String[] sortParams
    ) {
        String[] allowedProperties = {"id", "dateTime","rating","movieId"};
        List<Sort.Order> orders = createSort(sortParams, allowedProperties);

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        List<Review> reviews = reviewService.findAll(movieId, rating,pageable);
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(reviews);
        }
    }

    @GetMapping("{id}")
    @Operation(summary = "View a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the review"),
            @ApiResponse(responseCode = "404", description = "The review you are trying to retrieve doesn't exist")
    })
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);
        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(review.get());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the review"),
            @ApiResponse(responseCode = "404", description = "The review you are trying to update doesn't exist")
    })
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review) {
        Optional<Review> existingReviewOptional = reviewService.findById(id);
        if (existingReviewOptional.isPresent()) {
            Review existingReview = existingReviewOptional.get();
            existingReview.setRating(review.getRating());
            existingReview.setText(review.getText());
            Review updatedReview = reviewService.save(existingReview);
            return ResponseEntity.ok(updatedReview);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
