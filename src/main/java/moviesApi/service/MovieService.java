package moviesApi.service;

import moviesApi.domain.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie save(Movie movie);

    void deleteById(Long id);

    long countByGenre(String genre);

    long countByReleaseYear(int year);

    List<Movie> filterMovies(String genre, Integer year, Long directorId, Long actorId);
}