package moviesApi.filter;

import moviesApi.domain.Person;

import java.time.LocalDate;
import java.util.stream.Stream;

public class PersonFilter {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }


    public static PersonFilter.PersonFilterBuilder builder() {
        return new PersonFilter.PersonFilterBuilder();
    }

    public static class PersonFilterBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;

        public PersonFilter.PersonFilterBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public PersonFilter build() {
            return new PersonFilter(this);
        }
    }

    private PersonFilter(PersonFilter.PersonFilterBuilder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.birthDate = builder.birthDate;
    }

    public Stream<Person> filter(Stream<Person> input) {
        return input
                .filter(person -> (id == null || person.getId().equals(id)))
                .filter(person -> (firstName == null || person.getFirstName().equalsIgnoreCase(firstName)))
                .filter(person -> (lastName == null || person.getLastName().equalsIgnoreCase(lastName)))
                .filter(person -> (birthDate == null || person.getBirthDate().equals(birthDate)));
    }
}
