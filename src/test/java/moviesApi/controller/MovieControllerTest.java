package moviesApi.controller;

import moviesApi.SecurityConfig;
import moviesApi.domain.Movie;
import moviesApi.domain.Review;
import moviesApi.dto.MovieRecord;
import moviesApi.service.MovieService;

import moviesApi.service.ReviewService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static moviesApi.util.ControllerHelp.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestEntityManager
@ComponentScan(basePackages = "moviesApi")
@Import(SecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieControllerTest {

    @Autowired
    private MovieController movieController;
    @Autowired
    private ReviewController reviewController;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateMovieWithTooSmallYear() {
        Movie movie = generateMovie();
        movie.setReleaseYear(0);
        ResponseEntity<?> response = movieController.createMovie(movie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateMovieWithTooBigYear() {
        Movie movie = generateMovie();
        movie.setReleaseYear(55555);
        ResponseEntity<?> response = movieController.createMovie(movie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateMovieWithWrongDirectorId() {
        Movie movie = generateMovie();
        movie.setDirectorId(null);
        ResponseEntity<?> response = movieController.createMovie(movie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
//    @Rollback(false)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateMovie() {
        Movie movie = generateMovie();
        movie.setDirectorId(12L);
        ResponseEntity<?> response = movieController.createMovie(movie);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Movie savedMovie = (Movie) response.getBody();
        assertNotNull(savedMovie);
        assertEquals(movie.getTitle(), savedMovie.getTitle());
        assertEquals(movie.getGenre(), savedMovie.getGenre());
        assertEquals(movie.getReleaseYear(), savedMovie.getReleaseYear());
        assertEquals(movie.getDirectorId(), savedMovie.getDirectorId());
        assertEquals(movie.getActorIds(), savedMovie.getActorIds());

        // Call the getMovieById method
        response = movieController.getMovieById(savedMovie.getId());
        MovieRecord movieRecord = (MovieRecord) response.getBody();
        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedMovie.getId(), movieRecord.getId());
        assertEquals(savedMovie.getTitle(), movieRecord.getTitle());
        assertEquals(savedMovie.getGenre(), movieRecord.getGenre());
        assertEquals(savedMovie.getDirectorId(), movieRecord.getDirector().getId());
        assertEquals(savedMovie.getReleaseYear(), movieRecord.getReleaseYear());

        ResponseEntity<?> deleteResponse = movieController.deleteMovie(savedMovie.getId());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetMovieById() {
        // Create a test movie
        Movie testMovie = generateMovie();

        entityManager.persist(testMovie);
        entityManager.flush();

        // Call the getMovieById method
        ResponseEntity<MovieRecord> response = (ResponseEntity<MovieRecord>) movieController.getMovieById(testMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MovieRecord movieRecord = response.getBody();
        assertEquals(testMovie.getId(), movieRecord.getId());
        assertEquals(testMovie.getTitle(), movieRecord.getTitle());
        assertEquals(testMovie.getGenre(), movieRecord.getGenre());
        assertEquals(testMovie.getDirectorId(), movieRecord.getDirector().getId());
        assertEquals(testMovie.getReleaseYear(), movieRecord.getReleaseYear());

        // Delete the test movie
        movieService.deleteById(testMovie.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateMovieWithIncorrectId() {
        Movie testMovie = generateMovie();
        Long wrongId = -1L;

        ResponseEntity<?> response = movieController.updateMovie(wrongId, testMovie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateMovieWithIncorrectGenre() {
        Movie testMovie = generateMovie();
        entityManager.persist(testMovie);
        entityManager.flush();

        ResponseEntity<?> response = movieController.getMovieById(testMovie.getId());
        MovieRecord movieRecord = (MovieRecord) response.getBody();
        testMovie.setGenre("");

        response = movieController.updateMovie(movieRecord.getId(), testMovie);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateMovie() {
        // Create a test movie
        Movie testMovie = generateMovie();

        entityManager.persist(testMovie);
        entityManager.flush();

        ResponseEntity<?> response = movieController.getMovieById(testMovie.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MovieRecord movieRecord = (MovieRecord) response.getBody();
        assertEquals(testMovie.getId(), movieRecord.getId());
        assertEquals(testMovie.getDirectorId(), movieRecord.getDirector().getId());
        assertEquals(testMovie.getTitle(), movieRecord.getTitle());
        assertEquals(testMovie.getGenre(), movieRecord.getGenre());

        List<Long> actorIds = new ArrayList<>(testMovie.getActorIds());
        testMovie.setTitle("Updated Movie Title");
        testMovie.setGenre("Updated Genre");
        testMovie.setReleaseYear(2001);
        testMovie.setDirectorId(2L);
        actorIds.add(3L);
        testMovie.setActorIds(actorIds);

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
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteMovieWithWrongId() {
        Long wrongId = -1L;
        ResponseEntity<?> response = movieController.deleteMovie(wrongId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        wrongId = 9999999L;
        response = movieController.deleteMovie(wrongId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteMovieById() {
        Movie testMovie = generateMovie();
        entityManager.persist(testMovie);
        entityManager.flush();

        ResponseEntity<?> response = movieController.deleteMovie(testMovie.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Optional<Movie> movieOptional = movieService.findById(testMovie.getId());
        assertFalse(movieOptional.isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddReview() {
        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        Review review = generateReview();
        review.setMovieId(movie.getId());

//        test adding review with wrong movieId
        Long wrongMovieId = -1L;
        ResponseEntity<?> response = movieController.addReview(wrongMovieId, review);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

//        test adding review with wrong rating
        review.setRating(13f);
        response = movieController.addReview(movie.getId(), review);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

//        test adding review
        review.setRating(6.5f);
        response = movieController.addReview(movie.getId(), review);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Review savedReview = (Review) response.getBody();

        reviewController.deleteReview(savedReview.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetReviewsByWrongMovieId() {
        Long wrongId = -1L;
        ResponseEntity<?> response = movieController.getReviewsByMovieId(wrongId, null, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetReviewsWithWrongDateFilter() {
        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        LocalDateTime wrongDate = LocalDateTime.of(1000, 10, 10, 20, 20);

        ResponseEntity<?> response = movieController.getReviewsByMovieId(movie.getId(), wrongDate, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetReviewsWithWrongSortParam() {
        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        ResponseEntity<?> response = movieController.getReviewsByMovieId(movie.getId(), null, null, null, 0, 50, new String[]{"id123", "asc"});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetReviewsByMovieIdWithNoReviews() {
        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        ResponseEntity<?> response = movieController.getReviewsByMovieId(movie.getId(), null, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetReviewsByMovieId() {
        Movie movie = generateMovie();
        entityManager.persist(movie);
        entityManager.flush();

        Review review1 = generateReview();
        review1.setMovieId(movie.getId());
        reviewService.save(movie.getId(), review1);

        Review review2 = generateReview();
        review2.setMovieId(movie.getId());
        reviewService.save(movie.getId(), review2);

        ResponseEntity<?> response = movieController.getReviewsByMovieId(movie.getId(), null, null, null, 0, 50, new String[]{"id", "asc"});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Review> reviews = (List<Review>) response.getBody();
        assertEquals(2, reviews.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllMoviesWithWrongSortOrder() {
        ResponseEntity<?> response = movieController.getAllMovies(null, null, null, null, null, 0, 50, new String[]{"id", "asc123"});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllMoviesWithWrongGenre() {
        String wrongGenre = "someWrongGenre";
        ResponseEntity<?> response = movieController.getAllMovies(null, new String[]{wrongGenre}, null, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllMoviesWithWrongSortParameter() {
        ResponseEntity<?> response = movieController.getAllMovies(null, null, null, null, null, 0, 50, new String[]{"wrongValue", "asc"});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllMovies() {
        Movie movie1 = generateMovieWithParams("The Shawshank Redemption_test", "Drama", 1994, 1L, Arrays.asList(1L, 2L));
        Movie movie2 = generateMovieWithParams("The Godfather_test", "Crime", 1972, 2L, Arrays.asList(1L, 3L));
        Movie movie3 = generateMovieWithParams("The Dark Knight_test", "Action", 2008, 3L, Arrays.asList(2L, 3L));

        entityManager.persist(movie1);
        entityManager.persist(movie2);
        entityManager.persist(movie3);
        entityManager.flush();

        // Call the getAllMovies method by part of title
        ResponseEntity<?> response = movieController.getAllMovies("_test", null, null, null, null, 0, 50, new String[]{"id", "asc"});
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<MovieRecord> movies = (List<MovieRecord>) response.getBody();
        List<Long> movieIds = movies.stream().map(MovieRecord::getId).collect(Collectors.toList());
        Assert.assertEquals(3, movies.size());
        assertTrue(movieIds.contains(movie1.getId()));
        assertTrue(movieIds.contains(movie2.getId()));
        assertTrue(movieIds.contains(movie3.getId()));

        // Call the getAllMovies method with genre filter
        response = movieController.getAllMovies(null, new String[]{"Action"}, null, null, null, 0, 10, new String[]{"id", "asc"});
        assertEquals(HttpStatus.OK, response.getStatusCode());

        movies = (List<MovieRecord>) response.getBody();
        movieIds = movies.stream().map(MovieRecord::getId).collect(Collectors.toList());
        assertTrue(movies.size() >= 1);
        assertTrue(movieIds.contains(movie3.getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
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

        long count = (long) movieController.getCount(null, new String[]{"Comedy_test"}, null, null, null).getBody();
        assertEquals(3, count);

        count = (long) movieController.getCount(null, new String[]{"Comedy_test"}, 2002, null, null).getBody();
        assertEquals(1, count);

        count = (long) movieController.getCount(null, new String[]{"Comedy_test"}, null, 2L, null).getBody();
        assertEquals(2, count);

        count = (long) movieController.getCount(null, new String[]{"Comedy_test"}, null, null, new Long[]{3L}).getBody();
        assertEquals(3, count);

        count = (long) movieController.getCount(null, new String[]{"Horror_test"}, null, null, null).getBody();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteAllReviewsByMovieId() {
        // Create a test movie
        Movie testMovie = generateMovie();
        entityManager.persist(testMovie);
        entityManager.flush();

        List<Review> reviews = new ArrayList<>();
        // Create some reviews for the test movie
        for (int i = 0; i < 3; i++) {
            Review review = generateReview(testMovie.getId());
            reviews.add(reviewService.save(testMovie.getId(), review));
        }

        Long wrongId = -1L;
        // Call the deleteAllReviewsByMovieId method with wrong movieId
        ResponseEntity<?> response = movieController.deleteAllReviewsByMovieId(wrongId);
        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Call the deleteAllReviewsByMovieId method
        response = movieController.deleteAllReviewsByMovieId(testMovie.getId());

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(3, Integer.parseInt(response.getHeaders().get("X-Deleted-Count").get(0)));

        // Check that the reviews have been deleted
        for (Review review : reviews) {
            assertTrue(reviewService.findById(review.getId()).isEmpty());
        }
    }
}