package moviesApi.service;

import moviesApi.domain.Person;
import moviesApi.dto.PersonRecord;
import moviesApi.filter.PersonFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person save(Person person);

    void deleteById(Long id);

    List<Person> findAll(PersonFilter personFilter, Pageable pageable);

    Person update(Long id, Person person);

    Optional<Person> findById(Long id);

    boolean validatePerson(Person person);

    long count(PersonFilter personFilter);

    List<PersonRecord> getSummary(PersonFilter personFilter,Pageable pageable);
}
