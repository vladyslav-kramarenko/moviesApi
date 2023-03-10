package moviesApi.service;

import moviesApi.domain.Movie;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie save(Movie movie);

    void deleteById(Long id);

    long count(String genre, Integer year, Long directorId, Long actorId);

    List<Movie> filterMovies(String genre, Integer year, Long directorId, Long actorId, Pageable pageable);
}