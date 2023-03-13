package moviesApi.controller;

import moviesApi.domain.Movie;
import moviesApi.service.MovieService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
//@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3307/movies",
        "spring.datasource.username=user",
        "spring.datasource.password=app_password"
})
@ComponentScan(basePackages = "moviesApi")
class MovieControllerTest {

    @Autowired
    private MovieController movieController;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
//    @Rollback(false)
    public void testCreateMovie() {
        Movie movie = generateMovie();

        ResponseEntity<?> response = movieController.createMovie(movie);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Movie savedMovie = (Movie) response.getBody();
        assert savedMovie != null;
        assertEquals(movie.getTitle(), savedMovie.getTitle());
        assertEquals(movie.getGenre(), savedMovie.getGenre());
        assertEquals(movie.getReleaseYear(), savedMovie.getReleaseYear());
        assertEquals(movie.getDirectorId(), savedMovie.getDirectorId());
        assertEquals(movie.getActorIds(), savedMovie.getActorIds());

        // Call the getMovieById method
        response = movieController.getMovieById(savedMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedMovie, response.getBody());

        ResponseEntity<Void> deleteResponse = movieController.deleteMovie(savedMovie.getId());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }


    @Test
    public void testGetMovieById() {
        // Create a test movie
        Movie testMovie = generateMovie();

        entityManager.persist(testMovie);
        entityManager.flush();

        // Call the getMovieById method
        ResponseEntity<Movie> response = movieController.getMovieById(testMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMovie, response.getBody());

        // Delete the test movie
        movieService.deleteById(testMovie.getId());
    }

    @Test
    public void testDeleteMovieById() {
        // Create a test movie
        Movie testMovie = generateMovie();
        entityManager.persist(testMovie);
        entityManager.flush();

        // Call the deleteMovieById method
        ResponseEntity<Void> response = movieController.deleteMovie(testMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Check that the movie has been deleted
        Optional<Movie> movieOptional = movieService.findById(testMovie.getId());
        assertFalse(movieOptional.isPresent());
    }

    private static Movie generateMovie(){
        return generateMovieWithParams("Test Movie", "Genre", 2004, 1L, Arrays.asList(2L, 3L));
    }

    //Create a movie item with the given data
    private static Movie generateMovieWithParams(String title, String genre, Integer year, Long directorId, List<Long> actorIds) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setReleaseYear(year);
        movie.setDirectorId(directorId);
        movie.setActorIds(actorIds);
        return movie;
    }
}