package moviesApi.service.impl;

import io.micrometer.common.util.StringUtils;
import moviesApi.domain.Movie;
import moviesApi.repository.MovieRepository;
import moviesApi.service.MovieService;
import moviesApi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public List<Movie> findAll() {
        return movieRepository.findAll();
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
     * Counts the number of movies that match the given criteria.
     *
     * @param genre      the genre to filter by (can be null)
     * @param year       the release year to filter by (can be null)
     * @param directorId the ID of the director to filter by (can be null)
     * @param actorId    the ID of the actor to filter by (can be null)
     * @return the number of movies that match the given criteria
     */
    @Override
    public long count(String genre, Integer year, Long directorId, Long actorId) {
        return filter(movieRepository.findAll().stream(), genre, year, directorId, actorId).count();
    }

    /**
     * Filters the movies by the given parameters and returns a pageable list of movies.
     *
     * @param genre      the genre of the movie to filter by
     * @param year       the year of release of the movie to filter by
     * @param directorId the id of the director of the movie to filter by
     * @param actorId    the id of the actor of the movie to filter by
     * @param pageable   the pageable object to use for pagination and sorting
     * @return a pageable list of movies filtered by the given parameters
     */
    @Override
    public List<Movie> filterMovies(String genre, Integer year, Long directorId, Long actorId, Pageable pageable) {

        Stream<Movie> movieStream = movieRepository.findAll(pageable.getSort()).stream();
        movieStream = filter(movieStream, genre, year, directorId, actorId);

        return movieStream
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    /**
     * Filters the input stream of movies based on genre, year, director ID, and actor ID.
     *
     * @param input      the input stream of movies to be filtered
     * @param genre      the genre to filter by, or null if no filtering is desired by genre
     * @param year       the year to filter by, or null if no filtering is desired by year
     * @param directorId the director ID to filter by, or null if no filtering is desired by director ID
     * @param actorId    the actor ID to filter by, or null if no filtering is desired by actor ID
     * @return a filtered stream of movies
     */
    private Stream<Movie> filter(Stream<Movie> input, String genre, Integer year, Long directorId, Long actorId) {
        return input.filter(movie -> (genre == null || movie.getGenre().equalsIgnoreCase(genre)))
                .filter(movie -> (year == null || movie.getReleaseYear().equals(year)))
                .filter(movie -> (directorId == null || movie.getDirectorId().equals(directorId)))
                .filter(movie -> (actorId == null || movie.getActorIds().contains(actorId)));
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
}
