package moviesApi.service;

import moviesApi.domain.Person;
import moviesApi.filter.PersonFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person save(Person person);

    void deleteById(Long id);

    List<Person> findAll(PersonFilter personFilter, Pageable pageable);

    Optional<Person> findById(Long id);

    boolean validatePerson(Person person);

    long count(PersonFilter personFilter);
}
