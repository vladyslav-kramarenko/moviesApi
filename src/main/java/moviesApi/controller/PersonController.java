package moviesApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import moviesApi.domain.Person;
import moviesApi.dto.PersonRecord;
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

import static moviesApi.util.Constants.*;
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
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @Parameters({
            @Parameter(name = "firstName", description = "First Name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "lastName", description = "Last Name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "birthDate", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "birthDateFrom", description = "Birth Date From", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "birthDateTo", description = "Birth Date To", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = DEFAULT_PAGE)),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = DEFAULT_PAGE_SIZE)),
            @Parameter(
                    name = "sort",
                    description = "Sort reviews by property and order " +
                            "(allowed properties: id, firstName, lastName, birthDate; " +
                            "allowed order types: asc, desc)",
                    in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = DEFAULT_SORT
            ))
    })
    public ResponseEntity<?> getAllPersons(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "birthDate", required = false) LocalDate birthDate,
            @RequestParam(name = "birthDateFrom", required = false) LocalDate fromBirthDate,
            @RequestParam(name = "birthDateTo", required = false) LocalDate toBirthDate,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE, required = false) int page,
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(name = "sort", defaultValue = DEFAULT_SORT, required = false) String[] sortParams
    ) {
        try {
            PersonFilter personFilter = PersonFilter
                    .builder()
                    .withFirstName(firstName)
                    .withLastName(lastName)
                    .withBirthDate(birthDate)
                    .withToBirthDate(toBirthDate)
                    .withFromBirthDate(fromBirthDate)
                    .build();

            List<Sort.Order> orders = createSort(sortParams, ALLOWED_PERSON_SORT_PROPERTIES);

            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

            return ResponseEntity.ok(personService.findAll(personFilter, pageable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Count persons", description = "Returns the total count of persons that match the given filters.")
    @ApiResponse(responseCode = "200", description = "The number of persons that match the given filters")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    @Parameters({
            @Parameter(name = "id", description = "ID", in = ParameterIn.QUERY, schema = @Schema(type = "Integer")),
            @Parameter(name = "firstName", description = "First Name", in = ParameterIn.QUERY, schema = @Schema(type = "String")),
            @Parameter(name = "lastName", description = "Last Name", in = ParameterIn.QUERY, schema = @Schema(type = "String")),
            @Parameter(name = "birthDate", description = "Birth date", in = ParameterIn.QUERY, schema = @Schema(type = "Date")),
    })
    public ResponseEntity<?> getCount(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "birthDate", required = false) LocalDate birthDate
    ) {
        try {
            PersonFilter personFilter = PersonFilter
                    .builder()
                    .withFirstName(firstName)
                    .withLastName(lastName)
                    .withBirthDate(birthDate)
                    .build();

            return ResponseEntity.ok(personService.count(personFilter));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        try {
            return ResponseEntity.ok(personService.update(id, person));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person by id", description = "Delete a person by their id.")
    @ApiResponse(responseCode = "204", description = "Person deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        try {
            personService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get summary of people", description = "Returns a list of user records with amount of movies directed and acted in")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved summary",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PersonRecord.class)))
    @ApiResponse(responseCode = "204", description = "No content")
    @Parameters({
            @Parameter(name = "firstName", description = "First Name", in = ParameterIn.QUERY, schema = @Schema(type = "String")),
            @Parameter(name = "lastName", description = "Last Name", in = ParameterIn.QUERY, schema = @Schema(type = "String")),
            @Parameter(name = "page", description = "Page number (starting from 0)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = DEFAULT_PAGE)),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = DEFAULT_PAGE_SIZE))
    })
    public ResponseEntity<?> getSummary(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE, required = false) int page,
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PersonFilter personFilter = PersonFilter
                    .builder()
                    .withFirstName(firstName)
                    .withLastName(lastName)
                    .build();
            List<PersonRecord> personSummary = personService.getSummary(personFilter, pageable);
            if (personSummary == null || personSummary.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(personSummary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
