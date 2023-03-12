package moviesApi.controller;

import moviesApi.domain.Movie;
import moviesApi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // GET all movies
    @GetMapping("")
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "actor", required = false) Long actorId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String[] sortParams
    ) {
        List<Sort.Order> orders = new ArrayList<>();
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1].toUpperCase()) : Sort.Direction.ASC;
        orders.add(new Sort.Order(direction, sortField));

        // Validate the sort orders
        String[] allowedProperties = {"id", "release_year", "genre", "director_id"};
        for (Sort.Order order : orders) {
            if (!Arrays.asList(allowedProperties).contains(order.getProperty())) {
                throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
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
    @PostMapping("/add/movie")
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        // Create a new Movie object from the request data

        // Save the new movie to the database
        Movie savedMovie = movieService.save(movie);

        // Return the saved movie object in the response
        return ResponseEntity.ok(savedMovie);
    }

    // Retrieve a movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movieOptional = movieService.findById(id);
        return movieOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Edit a movie with ID
    @PutMapping("/edit/movie/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie updatedMovie) {
        Optional<Movie> movieOptional = movieService.findById(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            try {
                movie.setTitle(updatedMovie.getTitle());
                movie.setGenre(updatedMovie.getGenre());
                movie.setReleaseYear(updatedMovie.getReleaseYear());
                movie.setDirectorId(updatedMovie.getDirectorId());
                movie.setActorIds(updatedMovie.getActorIds());
                movieService.save(movie);
            }catch (Exception e){
                throw new IllegalArgumentException("Some movie property is invalid: " + updatedMovie);
            }
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE a movie by ID
    @DeleteMapping("/delete/{id}")
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
    public long getCountByGenre(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "actor", required = false) Long actorId) {
        return movieService.count(genre, year, directorId, actorId);
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "This is a test endpoint";
    }
}
