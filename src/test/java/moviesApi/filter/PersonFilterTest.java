package moviesApi.filter;

import moviesApi.domain.Person;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static moviesApi.util.TestHelper.generatePersonWithParams;
import static org.junit.Assert.assertEquals;

public class PersonFilterTest {
    @Test
    public void testFilter() {
        // Prepare sample data
        Person person1 = generatePersonWithParams( "John", "Doe", LocalDate.of(1990, 1, 1));
        Person person2 = generatePersonWithParams( "Jane", "Doe", LocalDate.of(1995, 1, 1));
        Person person3 = generatePersonWithParams( "Alice", "Smith", LocalDate.of(1990, 1, 1));

        Stream<Person> personStream = Stream.of(person1, person2, person3);

        // Configure filter
        PersonFilter personFilter = PersonFilter.builder()
                .withFirstName("John")
                .withLastName("Doe")
                .withBirthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Call filter method and collect results
        List<Person> filteredPersons = personFilter.filter(personStream).toList();

        // Assert the expected result
        assertEquals(1, filteredPersons.size());
        assertEquals(person1, filteredPersons.get(0));
    }
}
