package moviesApi.service;

import moviesApi.domain.Movie;

import moviesApi.filter.MovieFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie save(Movie movie);

    void deleteById(Long id);

    long count(MovieFilter movieFilter);

    List<Movie> filterMovies(MovieFilter movieFilter, Pageable pageable);

    boolean isValidMovie(Movie movie);
}