package moviesApi.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Movie {
    @Id
    private Long id;
    private String title;
    private String genre;
    private Integer releaseYear;
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
}