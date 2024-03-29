package moviesApi.filter;

import moviesApi.domain.Person;
import moviesApi.dto.PersonRecord;
import moviesApi.util.Constants;

import java.time.LocalDate;
import java.util.stream.Stream;

public class PersonFilter {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate fromBirthDate;
    private LocalDate toBirthDate;

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

    public LocalDate getFromBirthDate() {
        return fromBirthDate;
    }

    public LocalDate getToBirthDate() {
        return toBirthDate;
    }

    public static PersonFilter.PersonFilterBuilder builder() {
        return new PersonFilter.PersonFilterBuilder();
    }

    public static class PersonFilterBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private LocalDate toBirthDate;
        private LocalDate fromBirthDate;

        public PersonFilter.PersonFilterBuilder withId(Long id) {
            if (id != null && id < 0) {
                throw new IllegalArgumentException("ID should be positive");
            }
            this.id = id;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withFirstName(String firstName) {
            if (firstName != null && firstName.length() > Constants.MAX_FIRST_NAME_LENGTH) {
                throw new IllegalArgumentException("First Name must be under " + Constants.MAX_FIRST_NAME_LENGTH + " characters");
            }
            this.firstName = firstName;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withLastName(String lastName) {
            if (lastName != null && lastName.length() > Constants.MAX_LAST_NAME_LENGTH) {
                throw new IllegalArgumentException("Last Name must be under " + Constants.MAX_LAST_NAME_LENGTH + " characters");
            }
            this.lastName = lastName;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withBirthDate(LocalDate birthDate) {
            if (birthDate != null){
                if(birthDate.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("birth date must be in the past");
                }
            }
            this.birthDate = birthDate;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withFromBirthDate(LocalDate fromBirthDate) {
            if (fromBirthDate != null && fromBirthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("From birth date must be in the past");
            }
            this.fromBirthDate = fromBirthDate;
            return this;
        }

        public PersonFilter.PersonFilterBuilder withToBirthDate(LocalDate toBirthDate) {
            this.toBirthDate = toBirthDate;
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
        this.fromBirthDate = builder.fromBirthDate;
        this.toBirthDate = builder.toBirthDate;
    }

    public Stream<Person> filter(Stream<Person> input) {
        return input
                .filter(person -> (id == null || person.getId().equals(id)))
                .filter(person -> (firstName == null || person.getFirstName().equalsIgnoreCase(firstName)))
                .filter(person -> (lastName == null || person.getLastName().equalsIgnoreCase(lastName)))
                .filter(person -> (birthDate == null || person.getBirthDate().equals(birthDate)));
    }

    public Stream<PersonRecord> filterRecord(Stream<PersonRecord> input) {
        return input
                .filter(person -> (id == null || person.getId().equals(id)))
                .filter(person -> (firstName == null || person.getFirstName().equalsIgnoreCase(firstName)))
                .filter(person -> (lastName == null || person.getLastName().equalsIgnoreCase(lastName)));
//                .filter(person -> (birthDate == null || person.getBirthDate().equals(birthDate)));
    }
}
