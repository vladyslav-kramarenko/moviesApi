package moviesApi.controller;

import moviesApi.domain.Movie;
import moviesApi.domain.Person;
import moviesApi.domain.Review;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class controllerHelp {

    public static Movie generateMovie() {
        return generateMovieWithParams("Test Movie", "Genre", 2004, 1L, Arrays.asList(2L, 3L));
    }

    //Create a movie item with the given data
    public static Movie generateMovieWithParams(String title, String genre, Integer year, Long directorId, List<Long> actorIds) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setReleaseYear(year);
        movie.setDirectorId(directorId);
        movie.setActorIds(actorIds);
        return movie;
    }


    public static Review generateReview() {
        return generateReview(2L);
    }

    public static Review generateReview(Long movieId) {
        Review review = new Review();
        review.setRating(5);
        review.setText("Test review text");
        review.setMovieId(movieId);
        review.setDateTime(LocalDateTime.now());
        return review;

    }

    public static Person generatePerson() {
        return generatePersonWithParams("Test", "Person", LocalDate.of(2000, 1, 1));
    }

    // Create a person with the given data
    public static Person generatePersonWithParams(String firstName, String lastName, LocalDate birthDate) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setBirthDate(birthDate);
        return person;
    }
}
