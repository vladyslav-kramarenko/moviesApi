package moviesApi.controller;

import moviesApi.domain.Movie;
import moviesApi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.findAll();
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(movies);
        }
    }
    // Retrieve a movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movieOptional = movieService.findById(id);
        return movieOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

    // GET the count of movies by genre
    @GetMapping("/count/genre/{genre}")
    public long getCountByGenre(@PathVariable String genre) {
        return movieService.countByGenre(genre);
    }

    // GET the count of movies by release year
    @GetMapping("/count/year/{year}")
    public long getCountByReleaseYear(@PathVariable int year) {
        return movieService.countByReleaseYear(year);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Movie>> filterMovies(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "actor", required = false) Long actorId) {

        List<Movie> filteredMovies = movieService.filterMovies(genre, year, directorId, actorId);

        if (filteredMovies.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(filteredMovies);
        }
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "This is a test endpoint";
    }
}
