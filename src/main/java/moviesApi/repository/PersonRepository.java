package moviesApi.repository;

import moviesApi.domain.Person;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findById(Long id);

    void deleteById(Long id);

    List<Person> findAll(Sort sort);

    Person save(Person preson);
}
