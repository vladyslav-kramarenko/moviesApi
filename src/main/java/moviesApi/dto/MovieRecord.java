package moviesApi.dto;

import moviesApi.domain.Movie;
import moviesApi.domain.Person;
import java.util.Set;

public class MovieRecord {
    private Long id;
    private String title;
    private String genre;
    private Integer releaseYear;
    private Person director;

//    private Set<ReviewRecord> reviews = new HashSet();

    private Set<Person> actors;

    public MovieRecord() {
    }

    public MovieRecord(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.genre = movie.getGenre();
        this.releaseYear = movie.getReleaseYear();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

//    public void setReviews(Set<ReviewRecord> reviews) {
//        this.reviews = reviews;
//    }

    public void setActors(Set<Person> actors) {
        this.actors = actors;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Person getDirector() {
        return director;
    }

//    public Set<ReviewRecord> getReviews() {
//        return reviews;
//    }

    public Set<Person> getActors() {
        return actors;
    }
}
