package moviesApi.controller;

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
public class MovieController {
    private final MovieService movieService;
    private final ReviewService reviewService;

    @Autowired
    public MovieController(MovieService movieService, ReviewService reviewService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
    }

    // GET all movies
    @GetMapping("")
    public ResponseEntity<?> getAllMovies(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "actor", required = false) Long actorId,
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

    //Create a movie
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

    // Retrieve a movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movieOptional = movieService.findById(id);
        return movieOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Edit a movie with ID
    @PutMapping("/{id}")
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

    // DELETE a movie by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        Optional<Movie> movieOptional = movieService.findById(id);
        if (movieOptional.isPresent()) {
            movieService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET the count of movies
    @GetMapping("/count")
    public long getCount(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "actor", required = false) Long actorId) {
        return movieService.count(genre, year, directorId, actorId);
    }

    //Create a review
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

    // DELETE a review by ID
    @DeleteMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long movieId, @PathVariable Long reviewId) {
        Optional<Review> reviewOptional = reviewService.findById(reviewId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            if (review.getMovieId().equals(movieId)) {
                reviewService.deleteById(reviewId);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().body("Review does not belong to movie with ID: " + movieId);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET all reviews for a movie by ID
    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByMovieId(@PathVariable Long movieId) {
        Optional<Movie> movieOptional = movieService.findById(movieId);
        if (movieOptional.isPresent()) {
            List<Review> reviews = reviewService.findByMovieId(movieId);
            if(reviews.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete all reviews by movie id
    @DeleteMapping("/{movieId}/reviews")
    public ResponseEntity<?> deleteAllReviewsByMovieId(@PathVariable Long movieId) {
        if (movieService.findById(movieId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        int deleteCount=reviewService.deleteByMovieId(movieId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Deleted-Count", String.valueOf(deleteCount));
        return ResponseEntity.noContent().headers(headers).build();
    }
}
