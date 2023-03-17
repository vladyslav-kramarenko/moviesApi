package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import moviesApi.domain.Movie;
import moviesApi.domain.Review;
import moviesApi.filter.ReviewFilter;
import moviesApi.service.MovieService;
import moviesApi.service.ReviewService;
import moviesApi.filter.MovieFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static moviesApi.util.Utilities.createSort;

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
    @Parameters({
            @Parameter(name = "title", description = "Filter movies by title", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "genre", description = "Filter movies by genre", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "releaseYear", description = "Filter movies by release year", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "directorId", description = "Filter movies by director ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "actorIds", description = "Filter movies by actors ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", description = "Sort movies by property and order (allowed properties: id, release_year, genre, director_id; allowed order types: asc, desc)", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "id,asc"))
    })
    public ResponseEntity<?> getAllMovies(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "releaseYear", required = false) Integer year,
            @RequestParam(name = "directorId", required = false) Long directorId,
            @RequestParam(name = "actorIds", required = false) Long[] actorIds,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String[] sortParams
    ) {
        try {
            MovieFilter movieFilter = MovieFilter.builder()
                    .withTitle(title)
                    .withGenre(genre)
                    .withYear(year)
                    .withDirectorId(directorId)
                    .withActorIds(actorIds)
                    .build();

            String[] allowedProperties = {"id", "title", "releaseYear", "genre", "director_id"};
            List<Sort.Order> orders = createSort(sortParams, allowedProperties);

            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

            List<Movie> movies = movieService.filterMovies(movieFilter, pageable);

            if (movies.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(movies);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    @Operation(summary = "Create a movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movie data")
    })
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
    @Parameters({
            @Parameter(name = "title", description = "Filter movies by title", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "genre", description = "Filter movies by genre", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "year", description = "Filter movies by release year", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "directorId", description = "Filter movies by director ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "actorIds", description = "Filter movies by actors ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
    })
    public long getCount(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "directorId", required = false) Long directorId,
            @RequestParam(name = "actorIds", required = false) Long[] actorIds) {

        MovieFilter movieFilter = MovieFilter.builder()
                .withTitle(title)
                .withGenre(genre)
                .withYear(year)
                .withDirectorId(directorId)
                .withActorIds(actorIds)
                .build();

        return movieService.count(movieFilter);
    }

    @PostMapping("/{movieId}/reviews")
    @Operation(summary = "add review for a movie by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added review for the movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Invalid review data")
    })
    public ResponseEntity<?> addReview(@PathVariable Long movieId, @RequestBody Review review) {
        Optional<Movie> movieOptional = movieService.findById(movieId);
        if (movieOptional.isPresent()) {
            try {
                reviewService.validateReview(review);
                Movie movie = movieOptional.get();
                review.setMovieId(movie.getId());
                review.setDateTime(LocalDateTime.now());
                Review savedReview = reviewService.save(review);
                return ResponseEntity.ok(savedReview);
            } catch (IllegalArgumentException | ConstraintViolationException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{movieId}/reviews")
    @Operation(summary = "Get all reviews for a movie by ID")
    @ApiResponse(responseCode = "200", description = "Found reviews for the specified movie",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "204", description = "No reviews found for the specified movie")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @Parameters({
            @Parameter(name = "text", description = "Filter reviews by text", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "rating", description = "Filter reviews by rating", in = ParameterIn.QUERY, schema = @Schema(type = "float")),
            @Parameter(name = "date_time", description = "Filter reviews by date/time", in = ParameterIn.QUERY, schema = @Schema(type = "Date/time")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(
                    name = "sort",
                    description = "Sort reviews by property and order (allowed properties: id, dateTime; allowed order types: asc, desc)",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "dateTime,asc"
            ))
    })
    public ResponseEntity<?> getReviewsByMovieId(
            @PathVariable Long movieId,
            @RequestParam(name = "dateTime", required = false) LocalDateTime dateTime,
            @RequestParam(name = "rating", required = false) Float rating,
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "dateTime,asc") String[] sortParams
    ) {
        {
            try {
                String[] allowedProperties = {"id", "dateTime", "rating"};
                List<Sort.Order> orders = createSort(sortParams, allowedProperties);

                ReviewFilter reviewFilter = ReviewFilter.builder()
                        .withMovieId(movieId)
                        .withRating(rating)
                        .withDateTime(dateTime)
                        .withText(text)
                        .build();

                Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
                Optional<Movie> movieOptional = movieService.findById(movieId);

                if (movieOptional.isPresent()) {
                    List<Review> reviews = reviewService.findAll(reviewFilter, pageable);
                    if (reviews.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    }
                    return ResponseEntity.ok(reviews);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
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
