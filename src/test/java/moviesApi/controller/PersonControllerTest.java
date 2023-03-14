package moviesApi.controller;
import moviesApi.domain.Person;
import moviesApi.service.PersonService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
//@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3307/movies",
        "spring.datasource.username=user",
        "spring.datasource.password=app_password"
})
@ComponentScan(basePackages = "moviesApi")
class PersonControllerTest {

    @Autowired
    private PersonController personController;

    @Autowired
    private PersonService personService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreatePerson() {
        Person person = generatePerson();

        ResponseEntity<Person> response = personController.createPerson(person);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Person savedPerson = response.getBody();
        assert savedPerson != null;
        assertEquals(person.getFirstName(), savedPerson.getFirstName());
        assertEquals(person.getLastName(), savedPerson.getLastName());
        assertEquals(person.getBirthDate(), savedPerson.getBirthDate());

        // Call the getPersonById method
        response = personController.getPersonById(savedPerson.getId());

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedPerson, response.getBody());

        ResponseEntity<Void> deleteResponse = personController.deletePerson(savedPerson.getId());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }


    @Test
    public void testGetPersonById() {
        // Create a test person
        Person testPerson = generatePerson();

        entityManager.persist(testPerson);
        entityManager.flush();

        // Call the getPersonById method
        ResponseEntity<Person> response = personController.getPersonById(testPerson.getId());

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPerson, response.getBody());

        // Delete the test person
        personService.deleteById(testPerson.getId());
    }

    @Test
    public void testUpdatePerson() {
        // Create a test person
        Person testPerson = generatePerson();
        entityManager.persist(testPerson);
        entityManager.flush();

        // Update the test person
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setBirthDate(LocalDate.of(1980, 1, 1));

        // Call the updatePerson method
        ResponseEntity<Person> response = personController.updatePerson(testPerson.getId(), testPerson);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPerson, response.getBody());

        // Delete the test person
        personService.deleteById(testPerson.getId());
    }

    @Test
    public void testDeletePersonById() {
        // Create a test person
        Person testPerson = generatePerson();
        entityManager.persist(testPerson);
        entityManager.flush();

        // Call the deletePersonById method
        ResponseEntity<Void> response = personController.deletePerson(testPerson.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Check that the person has been deleted
        Optional<Person> personOptional = personService.findById(testPerson.getId());
        assertFalse(personOptional.isPresent());
    }

    private static Person generatePerson() {
        return generatePersonWithParams("Test", "Person", LocalDate.of(2000, 1, 1));
    }

    // Create a person with the given data
    private static Person generatePersonWithParams(String firstName, String lastName, LocalDate birthDate) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setBirthDate(birthDate);
        return person;
    }
}
