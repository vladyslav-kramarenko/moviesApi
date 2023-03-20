package moviesApi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import moviesApi.domain.Person;
import moviesApi.util.Constants;

public class PersonRecord {
    Long id;
    @NotNull(message = "First name cannot be blank")
    @Size(max = Constants.MAX_FIRST_NAME_LENGTH, message = "First name must be less than 64 characters")
    String firstName;
    @NotNull(message = "Last name cannot be blank")
    @Size(max = Constants.MAX_LAST_NAME_LENGTH, message = "Last name must be less than 64 characters")
    String LastName;
    int asActor;

    int asDirector;
    public PersonRecord() {
    }

    public PersonRecord(Long id, String firstName, String lastName, int asActor, int asDirector) {
        this.id = id;
        this.firstName = firstName;
        LastName = lastName;
        this.asActor = asActor;
        this.asDirector = asDirector;
    }

    public PersonRecord(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        LastName = person.getLastName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public int getAsActor() {
        return asActor;
    }

    public void setAsActor(int asActor) {
        this.asActor = asActor;
    }

    public int getAsDirector() {
        return asDirector;
    }

    public void setAsDirector(int asDirector) {
        this.asDirector = asDirector;
    }

}
