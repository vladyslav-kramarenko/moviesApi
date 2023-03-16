package moviesApi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import moviesApi.util.Constants;

import java.time.LocalDate;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "First name cannot be blank")
    @Size(max = Constants.MAX_FIRST_NAME_LENGTH, message = "First name must be less then 64 characters")
    private String firstName;
    @NotNull(message = "Last name cannot be blank")
    @Size(max = Constants.MAX_LAST_NAME_LENGTH, message = "Last name must be less then 64 characters")
    private String lastName;
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
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
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}