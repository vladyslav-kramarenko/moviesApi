package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import moviesApi.domain.Person;
import moviesApi.filter.PersonFilter;
import moviesApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static moviesApi.util.Utilities.createSort;

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
    @Parameters({
            @Parameter(name = "id", description = "ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "firstName", description = "First Name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "lastName", description = "Last Name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "birthDate", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(
                    name = "sort",
                    description = "Sort reviews by property and order " +
                            "(allowed properties: id, firstName, lastName, birthDate; " +
                            "allowed order types: asc, desc)",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "dateTime,asc"
            ))
    })
    public List<Person> getAllPersons(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "birthDate", required = false) LocalDate birthDate,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "sort", defaultValue = "id,asc", required = false) String[] sortParams
    ) {
        PersonFilter personFilter = PersonFilter
                .builder()
                .withId(id)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withBirthDate(birthDate)
                .build();

        String[] allowedProperties = {"id", "firstName", "lastName", "birthDate"};
        List<Sort.Order> orders = createSort(sortParams, allowedProperties);

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        return personService.findAll(personFilter, pageable);
    }

    @GetMapping("/count")
    @Operation(summary = "Count persons", description = "Returns the total count of persons that match the given filters.")
    @ApiResponse(responseCode = "200", description = "The number of persons that match the given filters")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @Parameters({
            @Parameter(name = "id", description = "ID", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "firstName", description = "First Name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "lastName", description = "Last Name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "birthDate", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
    })
    public Long getCount(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "birthDate", required = false) LocalDate birthDate
    ) {
        PersonFilter personFilter = PersonFilter
                .builder()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withBirthDate(birthDate)
                .build();

        return personService.count(personFilter);
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
    public ResponseEntity<?> createPerson(@Valid @RequestBody Person person) {
        try {
            personService.validatePerson(person);
            Person savedPerson = personService.save(person);
            return ResponseEntity.ok(savedPerson);
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
