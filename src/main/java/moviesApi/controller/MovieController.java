package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import moviesApi.domain.Movie;
import moviesApi.domain.Review;
import moviesApi.service.MovieService;
import moviesApi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie Controller", description = "APIs for managing movies")
public class MovieController {
    private final MovieService movieService;
    private final ReviewService reviewService;

    @Autowired
    public MovieController(MovieService movieService, ReviewService reviewService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
    }

    @GetMapping("")
    @Operation(summary = "Get all movies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of movies"),
            @ApiResponse(responseCode = "204", description = "No movies found"),
            @ApiResponse(responseCode = "400", description = "Invalid sort property or sort order")
    })
    public ResponseEntity<?> getAllMovies(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director_id", required = false) Long directorId,
            @RequestParam(name = "actor_id", required = false) Long actorId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String[] sortParams
    ) {
        List<Sort.Order> orders = new ArrayList<>();

        try {
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1].toUpperCase()) : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, sortField));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid sort order: " + sortParams[1]);
        }

        // Validate the sort orders
        String[] allowedProperties = {"id", "release_year", "genre", "director_id"};
        for (Sort.Order order : orders) {
            if (!Arrays.asList(allowedProperties).contains(order.getProperty())) {
                return ResponseEntity.badRequest().body("Invalid sort property: " + order.getProperty());
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        System.out.println("Pageable: " + pageable);
        List<Movie> movies = movieService.filterMovies(genre, year, directorId, actorId, pageable);
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(movies);
        }
    }

    @Operation(summary = "Create a movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movie data")
    })
    @PostMapping("")
    public ResponseEntity<?> createMovie(@RequestBody Movie movie) {
        try {
            movieService.isValidMovie(movie);
            Movie savedMovie = movieService.save(movie);
            return ResponseEntity.ok(savedMovie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a movie by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Found the movie", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class))
            }),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movieOptional = movieService.findById(id);
        return movieOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a movie by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Movie edited successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie updatedMovie) {
        Optional<Movie> movieOptional = movieService.findById(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            try {
                movieService.isValidMovie(updatedMovie);
                movie.setTitle(updatedMovie.getTitle());
                movie.setGenre(updatedMovie.getGenre());
                movie.setReleaseYear(updatedMovie.getReleaseYear());
                movie.setDirectorId(updatedMovie.getDirectorId());
                movie.setActorIds(updatedMovie.getActorIds());
                movieService.save(movie);
                return ResponseEntity.ok(movie);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        Optional<Movie> movieOptional = movieService.findById(id);
        if (movieOptional.isPresent()) {
            movieService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Get the count of movies", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the count of movies"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public long getCount(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "actor", required = false) Long actorId) {
        return movieService.count(genre, year, directorId, actorId);
    }

    @Operation(summary = "Get all reviews for a movie by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all reviews for the movie"),
            @ApiResponse(responseCode = "404", description = "reviews not found")
    })
    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long movieId, @RequestBody Review review) {
        Optional<Movie> movieOptional = movieService.findById(movieId);
        if (movieOptional.isPresent()) {
            try {
                reviewService.validateReview(review);
            } catch (IllegalArgumentException e) {
                ResponseEntity.badRequest().body(e.getMessage());
            }
            Movie movie = movieOptional.get();
            review.setMovieId(movie.getId());
            review.setDateTime(LocalDateTime.now());
            reviewService.save(review);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @DeleteMapping("/{movieId}/reviews/{reviewId}")
//    @Operation(summary = "Delete a review by ID")
//    @ApiResponse(responseCode = "204", description = "Review deleted successfully")
//    @ApiResponse(responseCode = "400", description = "Review does not belong to the specified movie ID",
//            content = @Content(mediaType = "text/plain"))
//    @ApiResponse(responseCode = "404", description = "Review not found")
//    public ResponseEntity<?> deleteReview(@PathVariable Long movieId, @PathVariable Long reviewId) {
//        Optional<Review> reviewOptional = reviewService.findById(reviewId);
//        if (reviewOptional.isPresent()) {
//            Review review = reviewOptional.get();
//            if (review.getMovieId().equals(movieId)) {
//                reviewService.deleteById(reviewId);
//                return ResponseEntity.noContent().build();
//            } else {
//                return ResponseEntity.badRequest().body("Review does not belong to movie with ID: " + movieId);
//            }
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/{movieId}/reviews")
    @Operation(summary = "Get all reviews for a movie by ID")
    @ApiResponse(responseCode = "200", description = "Found reviews for the specified movie",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "204", description = "No reviews found for the specified movie")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    public ResponseEntity<List<Review>> getReviewsByMovieId(@PathVariable Long movieId) {
        Optional<Movie> movieOptional = movieService.findById(movieId);
        if (movieOptional.isPresent()) {
            List<Review> reviews = reviewService.findByMovieId(movieId);
            if (reviews.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{movieId}/reviews")
    @Operation(summary = "Delete all reviews by movie ID")
    @ApiResponse(responseCode = "204", description = "Reviews deleted successfully")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    public ResponseEntity<?> deleteAllReviewsByMovieId(@PathVariable Long movieId) {
        if (movieService.findById(movieId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        int deleteCount = reviewService.deleteByMovieId(movieId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Deleted-Count", String.valueOf(deleteCount));
        return ResponseEntity.noContent().headers(headers).build();
    }
}
