package moviesApi.repository;

import moviesApi.domain.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMovieId(Long movieId, Sort sort);

    Optional<Review> findById(Long id);

    void deleteById(Long id);

    int deleteByMovieId(Long movieId);

    List<Review> findAll(Sort sort);

    List<Review> findByDateTimeAfter(LocalDateTime afterDateTime, Sort sort);

    List<Review> findByDateTimeBefore(LocalDateTime fromDateTime, Sort sort);

    List<Review> findByDateTimeBetween(LocalDateTime fromDateTime, LocalDateTime afterDateTime, Sort sort);

    Review save(Review review);
}
