package moviesApi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Title cannot be blank")
    @Size(min = 1, max = 255,message = "Title must be between 1 and 255 characters")
    private String title;
    @NotNull(message = "Genre cannot be blank")
    private String genre;
    @NotNull(message = "Release year cannot be blank")
    @Min(value = 1895, message = "Release year must be between 1895 and 9999")
    @Max(value = 9999, message = "Release year must be between 1895 and 9999")
    private Integer releaseYear;
    @NotNull(message = "Director id cannot be blank")
    @Positive
    private Long directorId;
    @ElementCollection
    @CollectionTable(name = "actor_ids")
    @Column(name = "actor_id")
    private List<Long> actorIds;


    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public List<Long> getActorIds() {
        return actorIds;
    }

    public void setActorIds(List<Long> actorIds) {
        this.actorIds = actorIds;
    }

    @Override
    public String toString() {
        return "title: " + getTitle()
                + "; genre: " + getGenre()
                + "; year: " + getReleaseYear()
                + "; director: " + getDirectorId()
                + "; actors: " + getActorIds();
    }
}