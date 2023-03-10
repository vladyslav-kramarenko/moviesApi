package moviesApi.service;

import moviesApi.domain.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> findAll();
}