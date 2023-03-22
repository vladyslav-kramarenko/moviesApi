package moviesApi.service.impl;

import io.micrometer.common.util.StringUtils;
import moviesApi.domain.Person;
import moviesApi.dto.PersonRecord;
import moviesApi.filter.PersonFilter;
import moviesApi.repository.PersonRepository;
import moviesApi.service.PersonService;
import moviesApi.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
        validatePerson(person);
        return personRepository.save(person);
    }

    @Override
    public Person update(Long id, Person person) {
        Optional<Person> existingPersonOptional = findById(id);
        if (existingPersonOptional.isEmpty()) {
            throw new IllegalArgumentException("Can't find a movie with provided ID");
        } else {
            Person existingPerson = existingPersonOptional.get();
            if (person.getFirstName() != null) {
                existingPerson.setFirstName(person.getFirstName());
            }
            if (person.getLastName() != null) {
                existingPerson.setLastName(person.getLastName());
            }
            if (person.getBirthDate() != null) {
                existingPerson.setBirthDate(person.getBirthDate());
            }
            return save(existingPerson);
        }
    }

    @Override
    public Optional<Person> findById(Long id) {
        Utilities.validateId(id);
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
        Optional<Person> personOptional = findById(id);
        if (personOptional.isEmpty()) {
            throw new IllegalArgumentException("can't find person with the provided ID");
        }
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
        Stream<Person> personStream;
        if (personFilter.getFromBirthDate() != null && personFilter.getToBirthDate() != null) {
            personStream = personRepository.findByBirthDateBetween(
                    personFilter.getFromBirthDate(), personFilter.getToBirthDate(), pageable.getSort()).stream();
        } else if (personFilter.getFromBirthDate() != null) {
            personStream = personRepository.findByBirthDateAfter(
                    personFilter.getFromBirthDate(), pageable.getSort()).stream();
        } else if (personFilter.getToBirthDate() != null) {
            personStream = personRepository.findByBirthDateBefore(
                    personFilter.getToBirthDate(), pageable.getSort()).stream();
        } else {
            personStream = personRepository.findAll(pageable.getSort()).stream();
        }
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

    /**
     * Retrieves a summary of each person record with the amount of movies they acted in and directed.
     *
     * @return a list of {@link PersonRecord} objects containing the person's ID, first name, last name, amount of movies acted in, and amount of movies directed.
     */
    @Override
    public List<PersonRecord> getSummary(PersonFilter personFilter, Pageable pageable) {
        List<Object[]> results = personRepository.getUserRecords();
        List<PersonRecord> userRecords = new ArrayList<>();

        for (Object[] row : results) {
            Long id = ((Integer) row[0]).longValue();
            String firstName = (String) row[1];
            String lastName = (String) row[2];
            int asDirector = ((Number) row[3]).intValue();
            int asActor = ((Number) row[4]).intValue();

            PersonRecord personRecord = new PersonRecord(id, firstName, lastName, asActor, asDirector);
            userRecords.add(personRecord);
        }
        return personFilter.filterRecord(userRecords.stream())
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }
}
