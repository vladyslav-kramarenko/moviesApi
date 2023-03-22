package moviesApi.controller;

import moviesApi.SecurityConfig;
import moviesApi.domain.Person;
import moviesApi.service.PersonService;

import moviesApi.util.Constants;
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

import static moviesApi.util.TestHelper.generatePerson;
import static moviesApi.util.TestHelper.generateStringBySize;
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
    public void testGetAllPersonsWithWrongBirthDateParameter() {
        LocalDate wrongBirthDate = LocalDate.of(3000, 1, 1);
        ResponseEntity<?> response = personController.getAllPersons(
                null, null, wrongBirthDate, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllPersons() {
        // Create some test persons
        Person person1 = generatePerson();

        entityManager.persist(person1);
        entityManager.flush();

        // Call the getAllPersons method
        ResponseEntity<?> response = personController.getAllPersons(
                null, null, null, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Person> persons = (List<Person>) response.getBody();
        // Verify the response
        assertFalse(persons.isEmpty());
        assertTrue(persons.contains(person1));

        // Delete the test persons
        personService.deleteById(person1.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetCountWithTooBigFirstName() {
        String wrongName = generateStringBySize(Constants.MAX_FIRST_NAME_LENGTH + 1);
        ResponseEntity<?> response = personController.getCount(
                wrongName, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetCount() {
        // Create some test persons
        Person person1 = generatePerson();
        person1.setFirstName("testNameTest");

        entityManager.persist(person1);
        entityManager.flush();

        // Call the getAllPersons method
        ResponseEntity<?> response = personController.getCount(
                person1.getFirstName(), null, null);
        // Verify the response
        assertEquals(1L, response.getBody());

        // Delete the test persons
        personService.deleteById(person1.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePersonWithNullFirstName() {
        Person person = generatePerson();
        person.setFirstName(null);
        ResponseEntity<?> response = personController.createPerson(person);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePersonWithWrongBirthDate() {
        Person person = generatePerson();
        person.setBirthDate(LocalDate.of(99999, 10, 10));
        ResponseEntity<?> response = personController.createPerson(person);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePersonWithTooLongLastName() {
        Person person = generatePerson();
        person.setLastName(generateStringBySize(Constants.MAX_LAST_NAME_LENGTH + 1));
        ResponseEntity<?> response = personController.createPerson(person);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePerson() {
        Person person = generatePerson();
        ResponseEntity<?> response = personController.createPerson(person);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Person savedPerson = (Person) response.getBody();
        assert savedPerson != null;
        assertEquals(person.getFirstName(), savedPerson.getFirstName());
        assertEquals(person.getLastName(), savedPerson.getLastName());
        assertEquals(person.getBirthDate(), savedPerson.getBirthDate());

        // Call the getPersonById method
        response = personController.getPersonById(savedPerson.getId());

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedPerson, response.getBody());

        ResponseEntity<?> deleteResponse = personController.deletePerson(savedPerson.getId());
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
    public void testUpdatePersonWithWrongId() {
        Person testPerson = generatePerson();
        Long wrongId = -1L;
        ResponseEntity<?> response = personController.updatePerson(wrongId, testPerson);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdatePerson() {
        Person testPerson = generatePerson();
        entityManager.persist(testPerson);
        entityManager.flush();

        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setBirthDate(LocalDate.of(1980, 1, 1));

        ResponseEntity<?> response = personController.updatePerson(testPerson.getId(), testPerson);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPerson, response.getBody());

        personService.deleteById(testPerson.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeletePersonByWrongId() {
        Long wrongId = -1L;
        ResponseEntity<?> response = personController.deletePerson(wrongId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeletePersonById() {
        Person testPerson = generatePerson();
        entityManager.persist(testPerson);
        entityManager.flush();

        ResponseEntity<?> response = personController.deletePerson(testPerson.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Optional<Person> personOptional = personService.findById(testPerson.getId());
        assertFalse(personOptional.isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetSummary() {
        int wrongPage=-1;
        ResponseEntity<?> response = personController.getSummary(
                null, null, wrongPage, 10
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
