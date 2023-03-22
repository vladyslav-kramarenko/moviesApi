package moviesApi.filter;

import moviesApi.domain.Movie;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static moviesApi.util.TestHelper.generateMovieWithParams;
import static org.junit.Assert.assertEquals;

public class MovieFilterTest {
    @Test
    public void testFilter() {
        // Prepare sample data

        Movie movie1 = generateMovieWithParams("Movie1", "Action", 2000, 1L, Arrays.asList(1L, 2L));
        Movie movie2 = generateMovieWithParams("Another Movie", "Drama", 1995, 2L, Arrays.asList(1L, 3L));
        Movie movie3 = generateMovieWithParams("Different Movie", "Action", 2010, 1L, Arrays.asList(2L, 3L));

        Stream<Movie> movieStream = Stream.of(movie1, movie2, movie3);

        // Configure filter
        MovieFilter movieFilter = MovieFilter.builder()
                .withTitle("movie")
                .withGenre(new String[]{"Action"})
                .withFromYear(2005)
                .withDirectorId(1L)
                .build();

        // Call filter method and collect results
        List<Movie> filteredMovies = movieFilter.filter(movieStream).toList();

        // Assert the expected result
        assertEquals(1, filteredMovies.size());
        assertEquals(movie3, filteredMovies.get(0));
    }
}
