package moviesApi.service;

import moviesApi.domain.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person save(Person person);

    void deleteById(Long id);

    List<Person> findAll();

    Optional<Person> findById(Long id);

    boolean validatePerson(Person person);
}
