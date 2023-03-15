package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import moviesApi.domain.Person;
import moviesApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person Controller", description = "APIs for managing persons")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("")
    @Operation(summary = "Get all persons", description = "Get a list of all persons.")
    @ApiResponse(responseCode = "200", description = "List of persons retrieved successfully")
    public List<Person> getAllPersons() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a person by id", description = "Get a person by their id.")
    @ApiResponse(responseCode = "200", description = "Person retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Optional<Person> personOptional = personService.findById(id);
        return personOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(summary = "Create a new person", description = "Create a new person with the given information.")
    @ApiResponse(responseCode = "200", description = "Person created successfully")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        Person savedPerson = personService.save(person);
        return ResponseEntity.ok(savedPerson);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing person", description = "Update an existing person with the given information.")
    @ApiResponse(responseCode = "200", description = "Person updated successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        Optional<Person> existingPersonOptional = personService.findById(id);
        if (existingPersonOptional.isPresent()) {
            Person existingPerson = existingPersonOptional.get();
            existingPerson.setFirstName(person.getFirstName());
            existingPerson.setLastName(person.getLastName());
            existingPerson.setBirthDate(person.getBirthDate());
            Person updatedPerson = personService.save(existingPerson);
            return ResponseEntity.ok(updatedPerson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person by id", description = "Delete a person by their id.")
    @ApiResponse(responseCode = "204", description = "Person deleted successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        Optional<Person> personOptional = personService.findById(id);
        if (personOptional.isPresent()) {
            personService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
