package moviesApi.service.impl;

import io.micrometer.common.util.StringUtils;

import moviesApi.domain.Movie;
import moviesApi.repository.MovieRepository;
import moviesApi.service.MovieService;
import moviesApi.util.Constants;
import moviesApi.filter.MovieFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    /**
     * Counts the number of movies that match the given filter criteria.
     *
     * @param movieFilter the filter criteria to apply to the movies
     * @return the number of movies that match the filter criteria
     */
    @Override
    public long count(MovieFilter movieFilter) {
        return movieFilter
                .filter(movieRepository.findAll().stream())
                .count();
    }

    /**
     * Filters the list of movies based on the provided {@link MovieFilter} object and returns a pageable list of movies
     * according to the provided pagination and sorting criteria.
     *
     * @param movieFilter The {@link MovieFilter} object used to filter the movies.
     * @param pageable    The {@link Pageable} object used for pagination and sorting.
     * @return A pageable list of movies filtered according to the provided {@link MovieFilter} object and {@link Pageable} object.
     */
    @Override
    public List<Movie> filterMovies(MovieFilter movieFilter, Pageable pageable) {
        Stream<Movie> movieStream = movieRepository.findAll(pageable.getSort()).stream();
        return movieFilter
                .filter(movieStream)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    /**
     * Validates a movie object.
     *
     * @param movie the movie object to validate
     * @return {@code true} if the movie object is valid
     * @throws IllegalArgumentException if the movie object is invalid
     */
    public boolean isValidMovie(Movie movie) throws IllegalArgumentException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be empty");
        }
        if (StringUtils.isBlank(movie.getTitle())) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (StringUtils.isBlank(movie.getGenre())) {
            throw new IllegalArgumentException("Genre cannot be blank");
        }
        if (movie.getReleaseYear() == null || !Pattern.matches("^\\d{4}$", movie.getReleaseYear().toString())) {
            throw new IllegalArgumentException("Invalid release year format. Please use the format yyyy");
        }
        if (movie.getReleaseYear() < Constants.MIN_MOVIE_RELEASE_YEAR) {
            throw new IllegalArgumentException("Release year cannot be greater than " + Constants.MIN_MOVIE_RELEASE_YEAR);
        }
        if (movie.getReleaseYear() > Constants.MAX_MOVIE_RELEASE_YEAR) {
            throw new IllegalArgumentException("Release year cannot be less than " + Constants.MAX_MOVIE_RELEASE_YEAR);
        }
        if (movie.getDirectorId() == null) {
            throw new IllegalArgumentException("Director ID cannot be null");
        }
        if (movie.getActorIds() == null || movie.getActorIds().isEmpty()) {
            throw new IllegalArgumentException("At least one actor ID is required");
        }
        return true;
    }

    /**
     * Returns a list of maps where each map contains the count of movies for a particular genre.
     * The map key is the genre name and the value is the count of movies with that genre.
     *
     * @return a list of maps containing genre counts
     */
    public List<Map<String, Long>> getMovieCountByGenre() {
        return movieRepository.findAll().stream()
                .collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<Map<Integer, Long>> getMovieCountByReleaseYear() {
        return movieRepository.findAll().stream()
                .collect(Collectors.groupingBy(Movie::getReleaseYear, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<Map<Long, Long>> getMovieCountByDirectorID() {
        return movieRepository.findAll().stream()
                .collect(Collectors.groupingBy(Movie::getDirectorId, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
