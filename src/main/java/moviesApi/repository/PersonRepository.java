package moviesApi.repository;

import moviesApi.domain.Person;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findById(Long id);

    void deleteById(Long id);

    List<Person> findAll(Sort sort);

    Person save(Person preson);

    @Query(value = "SELECT p.id, p.first_name, p.last_name, " +
            "COUNT(DISTINCT d.id) AS as_director, " +
            "COUNT(DISTINCT a.movie_id) AS as_actor " +
            "FROM person p " +
            "LEFT JOIN movie d ON p.id = d.director_id " +
            "LEFT JOIN actor_ids a ON p.id = a.actor_id " +
            "GROUP BY p.id", nativeQuery = true)
    List<Object[]> getUserRecords();
}
