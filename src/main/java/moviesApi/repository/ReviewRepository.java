package moviesApi.repository;

import moviesApi.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMovieId(Long movieId);

    Optional<Review> findById(Long id);

    void deleteById(Long id);

    List<Review> findAll();

    Review save(Review review);
}
