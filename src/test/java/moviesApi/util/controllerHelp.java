package moviesApi.util;

import moviesApi.domain.Movie;
import moviesApi.domain.Person;
import moviesApi.domain.Review;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code controllerHelp} class provides utility methods for generating test objects such as {@code Movie}, {@code Person}, and {@code Review}.
 * It includes methods for generating movies, reviews, and persons with specific parameters.
 */
public class controllerHelp {
    /**
     * Generates a movie object with sample parameters.
     *
     * @return A Movie object with sample parameters.
     */
    public static Movie generateMovie() {
        return generateMovieWithParams("Test Movie", "Genre", 2004, 1L, Arrays.asList(2L, 3L));
    }

    public static Movie generateMovieWithParams(String title, String genre, Integer year, Long directorId, List<Long> actorIds) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setReleaseYear(year);
        movie.setDirectorId(directorId);
        movie.setActorIds(actorIds);
        return movie;
    }

    /**
     * Generates a default review object for a movie with ID=2.
     *
     * @return A Review object with sample parameters.
     */
    public static Review generateReview() {
        return generateReview(2L);
    }

    /**
     * Generates a review object for a movie with the specified movie ID.
     *
     * @param movieId The ID of the movie the review is for.
     * @return A Review object with the specified parameters.
     */
    public static Review generateReview(Long movieId) {
        Review review = new Review();
        review.setRating(5);
        review.setText("Test review text");
        review.setMovieId(movieId);
        review.setDateTime(LocalDateTime.now());
        return review;

    }

    /**
     * Generates a default person object with sample parameters.
     *
     * @return A Person object with sample parameters.
     */
    public static Person generatePerson() {
        return generatePersonWithParams("Test", "Person", LocalDate.of(2000, 1, 1));
    }

    public static Person generatePersonWithParams(String firstName, String lastName, LocalDate birthDate) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setBirthDate(birthDate);
        return person;
    }
}
