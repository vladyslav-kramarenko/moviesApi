package moviesApi.service.impl;

import io.micrometer.common.util.StringUtils;
import moviesApi.domain.Person;
import moviesApi.filter.PersonFilter;
import moviesApi.repository.PersonRepository;
import moviesApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    /**
     * Validates a Person object by checking if the first name, last name,
     * and birthdate are not blank or null and if the birthdate is not in the future.
     *
     * @param person the Person object to validate
     * @return true if the Person object is valid
     * @throws IllegalArgumentException if the first name, last name, or birthdate are blank or null,
     *                                  or if the birthdate is in the future
     */
    @Override
    public boolean validatePerson(Person person) throws IllegalArgumentException {
        if (StringUtils.isBlank(person.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (StringUtils.isBlank(person.getLastName())) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        if (person.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date cannot be blank");
        }
        if (person.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        return true;
    }

    @Override
    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    /**
     * Returns a pageable list of persons filtered by the given parameters.
     *
     * @param personFilter the PersonFilter object containing the filter parameters
     * @param pageable     the Pageable object containing pagination and sorting parameters
     * @return a pageable list of persons filtered by the given parameters
     */
    @Override
    public List<Person> findAll(PersonFilter personFilter, Pageable pageable) {
        Stream<Person> personStream = personRepository.findAll(pageable.getSort()).stream();
        return personFilter
                .filter(personStream)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    /**
     * Returns the count of persons matching the given filter criteria.
     *
     * @param personFilter the filter criteria to apply
     * @return the count of persons matching the filter criteria
     * @throws IllegalArgumentException if the provided filter is invalid
     */
    @Override
    public long count(PersonFilter personFilter) {
        Stream<Person> personStream = personRepository.findAll().stream();
        return personFilter
                .filter(personStream).count();
    }
}
