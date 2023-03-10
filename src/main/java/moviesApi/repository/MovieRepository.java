package moviesApi.repository;

import moviesApi.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAll();
    Optional<Movie> findById(Long id);
    Movie save(Movie movie);
    void deleteById(Long id);
}