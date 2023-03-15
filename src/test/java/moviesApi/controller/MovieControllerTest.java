package moviesApi.controller;

import moviesApi.domain.Movie;
import moviesApi.domain.Review;
import moviesApi.service.MovieService;

import moviesApi.service.ReviewService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static moviesApi.controller.controllerHelp.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3307/movies",
        "spring.datasource.username=user",
        "spring.datasource.password=app_password"
})
@ComponentScan(basePackages = "moviesApi")
//@WithMockUser(roles = "ADMIN")
class MovieControllerTest {

    @Autowired
    private MovieController movieController;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private TestEntityManager entityManager;

    @Test
//    @Rollback(false)
    public void testCreateMovie() {
        Movie movie = generateMovie();
        movie.setReleaseYear(0);

        ResponseEntity<?> response = movieController.createMovie(movie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        movie.setReleaseYear(2004);

        response = movieController.createMovie(movie);
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
    public void testUpdateMovie() {
        // Create a test movie
        Movie testMovie = generateMovie();

        entityManager.persist(testMovie);
        entityManager.flush();

        ResponseEntity<?> response = movieController.getMovieById(testMovie.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMovie, response.getBody());

        List<Long> actorIds = new ArrayList<>(testMovie.getActorIds());

        testMovie.setTitle("Updated Movie Title");
        testMovie.setGenre("");
        testMovie.setReleaseYear(2001);
        testMovie.setDirectorId(2L);
        actorIds.add(3L);
        testMovie.setActorIds(actorIds);

        Long wrongId = -1L;

        response = movieController.updateMovie(wrongId, testMovie);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = movieController.updateMovie(testMovie.getId(), testMovie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        testMovie.setGenre("Updated Genre");

        // Call the updateMovie method
        response = movieController.updateMovie(testMovie.getId(), testMovie);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Movie savedMovie = (Movie) response.getBody();
        assertEquals(testMovie.getTitle(), savedMovie.getTitle());
        assertEquals(testMovie.getGenre(), savedMovie.getGenre());
        assertEquals(testMovie.getReleaseYear(), savedMovie.getReleaseYear());
        assertEquals(testMovie.getDirectorId(), savedMovie.getDirectorId());
        assertEquals(testMovie.getActorIds(), savedMovie.getActorIds());
    }

    @Test
    public void testDeleteMovieById() {
        // Create a test movie
        Movie testMovie = generateMovie();
        entityManager.persist(testMovie);
        entityManager.flush();

        Long wrongId = -1L;

        // Call the deleteMovieById method with wrong id
        ResponseEntity<Void> response = movieController.deleteMovie(wrongId);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Call the deleteMovieById method
        response = movieController.deleteMovie(testMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Check that the movie has been deleted
        Optional<Movie> movieOptional = movieService.findById(testMovie.getId());
        assertFalse(movieOptional.isPresent());
    }

    @Test
    public void testAddReview() {
        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        Review review = generateReview();
        review.setMovieId(movie.getId());

        ResponseEntity<?> response = movieController.addReview(movie.getId(), review);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReviewsByMovieId() {
        Long wrongId = -1L;

        //Check response when can't find a movie provided id
        ResponseEntity<List<Review>> response = movieController.getReviewsByMovieId(wrongId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        //Check response when a movie with provided id doesn't have any reviews
        response = movieController.getReviewsByMovieId(movie.getId());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Review review1 = generateReview();
        review1.setMovieId(movie.getId());
        reviewService.save(review1);

        Review review2 = generateReview();
        review2.setMovieId(movie.getId());
        reviewService.save(review2);

        response = movieController.getReviewsByMovieId(movie.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }


    @Test
    public void testGetAllMovies() {
        Movie movie1 = generateMovieWithParams("The Shawshank Redemption_test", "Drama", 1994, 1L, Arrays.asList(1L, 2L));
        Movie movie2 = generateMovieWithParams("The Godfather_test", "Crime", 1972, 2L, Arrays.asList(1L, 3L));
        Movie movie3 = generateMovieWithParams("The Dark Knight_test", "Action", 2008, 3L, Arrays.asList(2L, 3L));

        entityManager.persist(movie1);
        entityManager.persist(movie2);
        entityManager.persist(movie3);
        entityManager.flush();

        // Call the getAllMovies method with wrong sort order
        ResponseEntity<?> response = movieController.getAllMovies(null, null, null, null, 0, 50, new String[]{"id", "asc123"});

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Call the getAllMovies method with wrong sort parameter
        response = movieController.getAllMovies(null, null, null, null, 0, 50, new String[]{"wrongValue", "asc"});

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Call the getAllMovies method
        response = movieController.getAllMovies(null, null, null, null, 0, 50, new String[]{"id", "asc"});

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Movie> movies = (List<Movie>) response.getBody();
        assertTrue(movies.size() >= 3);
        assertTrue(movies.contains(movie1));
        assertTrue(movies.contains(movie2));
        assertTrue(movies.contains(movie3));

        // Call the getAllMovies method with a filter
        response = movieController.getAllMovies("Action", null, null, null, 0, 10, new String[]{"id", "asc"});

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        movies = (List<Movie>) response.getBody();
        assertTrue(movies.size() >= 1);
        assertTrue(movies.contains(movie3));
    }

    @Test
    public void testGetCount() {
        Movie movie1 = generateMovieWithParams("Test Movie 1", "Comedy_test", 2000, 2L, Arrays.asList(2L, 3L));
        Movie movie2 = generateMovieWithParams("Test Movie 2", "Drama_test", 2001, 2L, Arrays.asList(2L, 3L));
        Movie movie3 = generateMovieWithParams("Test Movie 3", "Comedy_test", 2002, 3L, Arrays.asList(2L, 3L));
        Movie movie4 = generateMovieWithParams("Test Movie 4", "Action_test", 2003, 2L, Arrays.asList(2L, 3L));
        Movie movie5 = generateMovieWithParams("Test Movie 3", "Comedy_test", 2004, 2L, Arrays.asList(2L, 3L));

        entityManager.persist(movie1);
        entityManager.persist(movie2);
        entityManager.persist(movie3);
        entityManager.persist(movie4);
        entityManager.persist(movie5);
        entityManager.flush();

        long count = movieController.getCount("Comedy_test", null, null, null);

        assertEquals(3, count);

        count = movieController.getCount("Comedy_test", 2002, null, null);

        assertEquals(1, count);

        count = movieController.getCount("Comedy_test", null, 2L, null);

        assertEquals(2, count);

        count = movieController.getCount("Comedy_test", null, null, 3L);

        assertEquals(3, count);

        count = movieController.getCount("Horror_test", null, null, null);

        assertEquals(0, count);
    }

    @Test
    public void testDeleteAllReviewsByMovieId() {
        // Create a test movie
        Movie testMovie = generateMovie();
        entityManager.persist(testMovie);
        entityManager.flush();

        // Create some reviews for the test movie
        for (int i = 0; i < 3; i++) {
            reviewService.save(generateReview(testMovie.getId()));
        }

        // Call the deleteAllReviewsByMovieId method
        ResponseEntity<?> response = movieController.deleteAllReviewsByMovieId(testMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(3, Integer.parseInt(response.getHeaders().get("X-Deleted-Count").get(0)));

        // Check that the reviews have been deleted
        assertEquals(0, reviewService.findByMovieId(testMovie.getId()).size());
    }
}