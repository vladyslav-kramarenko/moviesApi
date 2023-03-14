package moviesApi.repository;

import moviesApi.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findReviewsByMovieId(Long movieId);

    Optional<Review> findReviewById(Long reviewId);

    void deleteReviewById(Long reviewId);

    List<Review> findAll();

    Review save(Review review);
}
