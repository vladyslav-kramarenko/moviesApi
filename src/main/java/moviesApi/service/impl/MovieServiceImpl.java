package moviesApi.service.impl;

import io.micrometer.common.util.StringUtils;

import moviesApi.domain.Movie;
import moviesApi.domain.Person;
import moviesApi.dto.MovieRecord;
import moviesApi.repository.MovieRepository;
import moviesApi.service.MovieService;
import moviesApi.service.PersonService;
import moviesApi.service.ReviewService;
import moviesApi.util.Constants;
import moviesApi.filter.MovieFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static moviesApi.util.Utilities.mapsToListOfSingletonMaps;
import static moviesApi.util.Utilities.validateId;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    private final ReviewService reviewService;
    private final PersonService personService;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, ReviewService reviewService, PersonService personService) {
        this.movieRepository = movieRepository;
        this.reviewService = reviewService;
        this.personService = personService;
    }

    @Override
    public Optional<MovieRecord> findRecordById(Long id) {
        Optional<Movie> movie = findById(id);
        if (movie.isEmpty()) return Optional.of(null);
        MovieRecord movieRecord = new MovieRecord(movie.get());
        movieRecord.setDirector(personService.findById(movie.get().getDirectorId()).get());
        movieRecord.setActors(getPersonsFromIds(movie.get().getActorIds()));
        return Optional.of(movieRecord);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        validateId(id);
        return movieRepository.findById(id);
    }

    @Override
    public Movie save(Movie movie) throws IllegalArgumentException {
        validateMovieCreation(movie);
        return movieRepository.save(movie);
    }

    @Override
    public Movie update(Long movieId, Movie updatedMovie) {
        validateId(movieId);
        Optional<Movie> movieOptional = findById(movieId);

        Movie movie = movieOptional.get();

        validateMovieUpdate(updatedMovie);

        if (updatedMovie.getTitle() != null) {
            movie.setTitle(updatedMovie.getTitle());
        }
        if (updatedMovie.getGenre() != null) {
            movie.setGenre(updatedMovie.getGenre());
        }
        if (updatedMovie.getReleaseYear() != null) {
            movie.setReleaseYear(updatedMovie.getReleaseYear());
        }
        if (updatedMovie.getDirectorId() != null) {
            movie.setDirectorId(updatedMovie.getDirectorId());
        }
        if (updatedMovie.getActorIds() != null) {
            movie.setActorIds(updatedMovie.getActorIds());
        }

        return movieRepository.save(movie);
    }

    @Override
    public void deleteById(Long id) {
        validateId(id);
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
     * @return A pageable list of {@link MovieRecord} filtered according to the provided {@link MovieFilter} object and {@link Pageable} object.
     */
    @Override
    public List<MovieRecord> filterMovies(MovieFilter movieFilter, Pageable pageable) {
        Stream<Movie> movieStream = movieRepository.findAll(pageable.getSort()).stream();
        List<MovieRecord> movieRecords =
                movieFilter
                        .filter(movieStream)
                        .skip(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .map(movie -> {
                            MovieRecord movieRecord = new MovieRecord(movie);
                            movieRecord.setDirector(personService.findById(movie.getDirectorId()).get());
                            movieRecord.setActors(getPersonsFromIds(movie.getActorIds()));
                            return movieRecord;
                        })
                        .collect(Collectors.toList());
        return movieRecords;
    }

    private Set<Person> getPersonsFromIds(List<Long> personIds) {
        return personIds.stream()
                .map(actor -> personService.findById(actor).get())
                .collect(Collectors.toSet());
    }

    /**
     * Validates a movie object.
     *
     * @param movie the movie object to validate
     * @return {@code true} if the movie object is valid
     * @throws IllegalArgumentException if the movie object is invalid
     */
    public static boolean validateMovieUpdate(Movie movie) throws IllegalArgumentException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be empty");
        }
        if (movie.getTitle() != null) {
            if (StringUtils.isBlank(movie.getTitle())) {
                throw new IllegalArgumentException("Title cannot be blank");
            }
            if (movie.getTitle().length() > Constants.MAX_TITLE_LENGTH) {
                throw new IllegalArgumentException("Maximum title length is " + Constants.MAX_TITLE_LENGTH);
            }
        }
        if (movie.getGenre() != null) {
            if (StringUtils.isBlank(movie.getGenre())) {
                throw new IllegalArgumentException("Genre cannot be blank");
            }
            if (movie.getGenre().length() > Constants.MAX_GENRE_LENGTH) {
                throw new IllegalArgumentException("Maximum Genre length is " + Constants.MAX_GENRE_LENGTH);
            }
        }
        if (movie.getReleaseYear() != null && !Pattern.matches("^\\d{4}$", movie.getReleaseYear().toString())) {
            throw new IllegalArgumentException("Invalid release year format. Please use the format yyyy");
        }
        if (movie.getReleaseYear() != null && movie.getReleaseYear() < Constants.MIN_MOVIE_RELEASE_YEAR) {
            throw new IllegalArgumentException("Release year cannot be greater than " + Constants.MIN_MOVIE_RELEASE_YEAR);
        }
        if (movie.getReleaseYear() != null && movie.getReleaseYear() > Constants.MAX_MOVIE_RELEASE_YEAR) {
            throw new IllegalArgumentException("Release year cannot be less than " + Constants.MAX_MOVIE_RELEASE_YEAR);
        }
        if (movie.getDirectorId() != null) {
            validateId(movie.getDirectorId());
        }
        if (movie.getActorIds() != null) {
            if (movie.getActorIds().isEmpty()) {
                throw new IllegalArgumentException("At least one actor ID is required");
            }
            for (Long actorId : movie.getActorIds()) {
                validateId(actorId);
            }
        }
        return true;
    }

    public static boolean validateMovieCreation(Movie movie) throws IllegalArgumentException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be empty");
        }
        if (movie.getReleaseYear() == null) {
            throw new IllegalArgumentException("Release year cannot be empty");
        }
        if (movie.getActorIds() == null || movie.getActorIds().isEmpty()) {
            throw new IllegalArgumentException("At least one actor ID is required");
        }
        if (movie.getDirectorId() == null) {
            throw new IllegalArgumentException("Director ID cannot be null");
        }
        if (StringUtils.isBlank(movie.getTitle())) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (StringUtils.isBlank(movie.getGenre())) {
            throw new IllegalArgumentException("Genre cannot be blank");
        }
        validateMovieUpdate(movie);
        return true;
    }

    /**
     * Returns a list of maps where each map contains the count of movies for a particular genre.
     * The map key is the genre name and the value is the count of movies with that genre.
     *
     * @return a list of maps containing genre counts
     */
    public List<Map<String, Long>> getMovieCountByGenre() {
        Map<String, Long> movieCount = movieRepository.findAll().stream()
                .collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()));
        return mapsToListOfSingletonMaps(movieCount);
    }

    public List<Map<Integer, Long>> getMovieCountByReleaseYear() {
        Map<Integer, Long> movieCount = movieRepository.findAll().stream()
                .collect(Collectors.groupingBy(Movie::getReleaseYear, Collectors.counting()));
        return mapsToListOfSingletonMaps(movieCount);
    }

    public List<Map<Long, Long>> getMovieCountByDirectorID() {
        Map<Long, Long> movieCount = movieRepository.findAll().stream()
                .collect(Collectors.groupingBy(Movie::getDirectorId, Collectors.counting()));
        return mapsToListOfSingletonMaps(movieCount);
    }
}
