package moviesApi.service.impl;

import moviesApi.domain.Movie;
import moviesApi.repository.MovieRepository;
import moviesApi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
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

    @Override
    public long count(String genre, Integer year, Long directorId, Long actorId) {
        return filter(movieRepository.findAll().stream(), genre, year, directorId, actorId).count();
    }

    @Override
    public List<Movie> filterMovies(String genre, Integer year, Long directorId, Long actorId, Pageable pageable) {

        Stream<Movie> movieStream = movieRepository.findAll(pageable.getSort()).stream();
        movieStream = filter(movieStream, genre, year, directorId, actorId);

        return movieStream
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    private Stream<Movie> filter(Stream<Movie> input, String genre, Integer year, Long directorId, Long actorId) {
        return input.filter(movie -> (genre == null || movie.getGenre().equalsIgnoreCase(genre)))
                .filter(movie -> (year == null || movie.getReleaseYear().equals(year)))
                .filter(movie -> (directorId == null || movie.getDirectorId().equals(directorId)))
                .filter(movie -> (actorId == null || movie.getActorIds().contains(actorId)));
    }
}
