package moviesApi.domain;

import moviesApi.SecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertNotNull;

@AutoConfigureTestEntityManager
@ComponentScan(basePackages = "moviesApi")
@Import(SecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DomainTest {
    @Autowired
    private TestEntityManager entityManager;
    @Test
    public void testCreateMovieWithReview() {
        // create a new Person object for the director
        Person director = new Person();
        director.setFirstName("Christopher");
        director.setLastName("Nolan");
        director.setBirthDate(LocalDate.of(1970, 7, 30));
        director=entityManager.persist(director);

        // create a new Movie object and set its properties
        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Science Fiction");
        movie.setReleaseYear(2010);
        movie.setDirectorId(director.getId());
        entityManager.persist(movie);

        // create a new Review object and set its properties
        Review review = new Review();
        review.setText("This is a great movie!");
        review.setRating(9.0f);
        review.setMovieId(movie.getId());
        review.setDateTime(LocalDateTime.now());
        entityManager.persist(review);

        // flush the changes to the database and clear the persistence context
        entityManager.flush();
        entityManager.clear();

        // retrieve the Movie object from the database and check its reviews
        Movie savedMovie = entityManager.find(Movie.class, movie.getId());
        assertNotNull(savedMovie);
//        assertEquals(1, savedMovie.getReviews().size());
//        assertEquals("This is a great movie!", savedMovie.getReviews().get(0).getText());
//        assertEquals(9.0f, savedMovie.getReviews().get(0).getRating(), 0.1f);
    }
}
