package moviesApi.service;

import moviesApi.domain.Movie;

import moviesApi.dto.MovieRecord;
import moviesApi.filter.MovieFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> findById(Long id);
    Optional<MovieRecord> findRecordById(Long id);

    Movie save(Movie movie);

    Movie update(Long movieId, Movie movie);

    void deleteById(Long id);

    long count(MovieFilter movieFilter);

    List<MovieRecord> filterMovies(MovieFilter movieFilter, Pageable pageable);

    List<Map<String, Long>> getMovieCountByGenre();

    List<Map<Integer, Long>> getMovieCountByReleaseYear();

    List<Map<String, Long>> getMovieCountByDirectorID();
}