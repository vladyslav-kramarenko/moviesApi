package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import moviesApi.domain.Review;
import moviesApi.filter.ReviewFilter;
import moviesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static moviesApi.util.Utilities.createSort;
import static moviesApi.util.Constants.*;

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
            @ApiResponse(responseCode = "400", description = "Invalid id")
    })
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    @Operation(summary = "View a list of all available reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of reviews"),
            @ApiResponse(responseCode = "404", description = "No reviews found")
    })
    @Parameters({
            @Parameter(name = "rating", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "movieId", description = "Filter reviews by movie ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "rating", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "ratingTo", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "ratingFrom", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "dateTime", description = "Filter reviews by date/time", in = ParameterIn.QUERY, schema = @Schema(type = "Date/time")),
            @Parameter(name = "fromDateTime", description = "Filter reviews by date/time", in = ParameterIn.QUERY, schema = @Schema(type = "Date/time")),
            @Parameter(name = "ToDateTime", description = "Filter reviews by date/time", in = ParameterIn.QUERY, schema = @Schema(type = "Date/time")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = DEFAULT_PAGE)),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = DEFAULT_PAGE_SIZE)),
            @Parameter(
                    name = "sort",
                    description = "Sort reviews by property and order (allowed properties: id, movieId,rating, dateTime; allowed order types: asc, desc)",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = DEFAULT_SORT
            ))
    })
    public ResponseEntity<?> getAllReviews(
            @RequestParam(name = "dateTime", required = false) LocalDateTime dateTime,
            @RequestParam(name = "movieId", required = false) Long movieId,
            @RequestParam(name = "rating", required = false) Float rating,
            @RequestParam(name = "fromDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fromDateTime,
            @RequestParam(name = "toDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime toDateTime,
            @RequestParam(name = "ratingFrom", required = false) Float ratingFrom,
            @RequestParam(name = "ratingTo", required = false) Float ratingTo,
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE, required = false) int page,
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(name = "sort", defaultValue = DEFAULT_SORT, required = false) String[] sortParams
    ) {
        try {
            String[] allowedProperties = {"id", "dateTime", "rating", "movieId"};
            List<Sort.Order> orders = createSort(sortParams, allowedProperties);

            ReviewFilter reviewFilter = ReviewFilter.builder()
                    .withRating(rating)
                    .withMovieId(movieId)
                    .withRatingFrom(ratingFrom)
                    .withRatingTo(ratingTo)
                    .withDateTime(dateTime)
                    .withFromDateTime(fromDateTime)
                    .withToDateTime(toDateTime)
                    .withText(text)
                    .build();

            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

            List<Review> reviews = reviewService.findAll(reviewFilter, pageable);

            if (reviews.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(reviews);
            }
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Count reviews", description = "Retrieves the count of reviews based on provided filter parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the count of reviews"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter value")
    })
    @Parameters({
            @Parameter(name = "rating", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "movieId", description = "Filter reviews by movie ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "text", description = "Filter reviews by text", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "ratingTo", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "ratingFrom", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "dateTime", description = "Filter reviews by date/time", in = ParameterIn.QUERY, schema = @Schema(type = "Date/time")),
    })
    public ResponseEntity<?> getCount(
            @RequestParam(name = "dateTime", required = false) LocalDateTime dateTime,
            @RequestParam(name = "movieId", required = false) Long movieId,
            @RequestParam(name = "ratingFrom", required = false) Float ratingFrom,
            @RequestParam(name = "ratingTo", required = false) Float ratingTo,
            @RequestParam(name = "rating", required = false) Float rating,
            @RequestParam(name = "text", required = false) String text
    ) {
        try {
            ReviewFilter reviewFilter = ReviewFilter.builder()
                    .withDateTime(dateTime)
                    .withMovieId(movieId)
                    .withText(text)
                    .withRatingFrom(ratingFrom)
                    .withRatingTo(ratingTo)
                    .withDateTime(dateTime)
                    .withRating(rating)
                    .build();

            return ResponseEntity.ok(reviewService.count(reviewFilter));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}")
    @Operation(summary = "View a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the review"),
            @ApiResponse(responseCode = "404", description = "The review you are trying to retrieve doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter value")
    })
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            Optional<Review> review = reviewService.findById(id);
            if (review.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(review.get());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the review"),
            @ApiResponse(responseCode = "404", description = "The review you are trying to update doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter value")
    })
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Review review) {
        try {
            Optional<Review> updatedReview = reviewService.update(id, review);
            if (updatedReview.isPresent()) {
                return ResponseEntity.ok(updatedReview.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
