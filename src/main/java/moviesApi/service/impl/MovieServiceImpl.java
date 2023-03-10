package moviesApi.service.impl;

import moviesApi.domain.Movie;
import moviesApi.repository.MovieRepository;
import moviesApi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public long countByGenre(String genre) {
        return movieRepository.findAll().stream()
                .filter(movie -> movie.getGenre().equalsIgnoreCase(genre))
                .count();
    }

    @Override
    public long countByReleaseYear(int year) {
        return movieRepository.findAll().stream()
                .filter(movie -> movie.getReleaseYear() == year)
                .count();
    }
}
