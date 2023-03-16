package moviesApi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import moviesApi.util.Constants;

import java.util.List;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Title cannot be blank")
    @Size(max = Constants.MAX_TITLE_LENGTH,message = "Title must be less then 255 characters")
    private String title;
    @NotNull(message = "Genre cannot be blank")
    private String genre;
    @NotNull(message = "Release year cannot be blank")
    @Min(value = Constants.MIN_MOVIE_RELEASE_YEAR, message = "Release year must be greater than "+Constants.MIN_MOVIE_RELEASE_YEAR)
    @Max(value = Constants.MAX_MOVIE_RELEASE_YEAR, message = "Release year must less than "+Constants.MAX_MOVIE_RELEASE_YEAR)
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