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
        return movieRepository.findAll().stream()
                .filter(movie -> (genre == null || movie.getGenre().equalsIgnoreCase(genre)))
                .filter(movie -> (year == null || movie.getReleaseYear().equals(year)))
                .filter(movie -> (directorId == null || movie.getDirectorId().equals(directorId)))
                .filter(movie -> (actorId == null || movie.getActorIds().contains(actorId)))
                .count();
    }

    @Override
    public List<Movie> filterMovies(String genre, Integer year, Long directorId, Long actorId, Pageable pageable) {
        List<Movie> allMovies = movieRepository.findAll();

        return allMovies.stream()
                .filter(movie -> (genre == null || movie.getGenre().equalsIgnoreCase(genre)))
                .filter(movie -> (year == null || movie.getReleaseYear().equals(year)))
                .filter(movie -> (directorId == null || movie.getDirectorId().equals(directorId)))
                .filter(movie -> (actorId == null || movie.getActorIds().contains(actorId)))
                .collect(Collectors.toList());
    }
}
