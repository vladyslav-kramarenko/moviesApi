package moviesApi.controller;

import moviesApi.SecurityConfig;
import moviesApi.domain.Person;
import moviesApi.service.PersonService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static moviesApi.controller.controllerHelp.generatePerson;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestEntityManager
@ComponentScan(basePackages = "moviesApi")
@Import(SecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonControllerTest {
    @Autowired
    private PersonController personController;
    @Autowired
    private PersonService personService;
    @Autowired
    private TestEntityManager entityManager;
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllPersons() {
        // Create some test persons
        Person person1 = generatePerson();

        entityManager.persist(person1);
        entityManager.flush();

        // Call the getAllPersons method
        List<Person> persons = personController.getAllPersons();

        // Verify the response
        assertTrue(persons.size()>0);
        assertTrue(persons.contains(person1));

        // Delete the test persons
        personService.deleteById(person1.getId());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
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
    @WithMockUser(username = "admin", roles = "ADMIN")
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdatePerson() {
        // Create a test person
        Person testPerson = generatePerson();
        entityManager.persist(testPerson);
        entityManager.flush();

        // Update the test person
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setBirthDate(LocalDate.of(1980, 1, 1));


        Long wrongId = -1L;

        // Call the updatePerson method with wrong id
        ResponseEntity<Person> response = personController.updatePerson(wrongId, testPerson);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Call the updatePerson method
        response = personController.updatePerson(testPerson.getId(), testPerson);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPerson, response.getBody());

        // Delete the test person
        personService.deleteById(testPerson.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeletePersonById() {
        // Create a test person
        Person testPerson = generatePerson();
        entityManager.persist(testPerson);
        entityManager.flush();

        Long wrongId = -1L;

        // Call the deletePersonById method
        ResponseEntity<Void> response = personController.deletePerson(wrongId);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Call the deletePersonById method
        response = personController.deletePerson(testPerson.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Check that the person has been deleted
        Optional<Person> personOptional = personService.findById(testPerson.getId());
        assertFalse(personOptional.isPresent());
    }
}
