package moviesApi.service.impl;

import moviesApi.domain.Movie;
import moviesApi.util.Constants;
import moviesApi.util.TestHelper;
import org.junit.Test;

import java.util.List;

import static moviesApi.util.TestHelper.generateStringBySize;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class MovieServiceImplTest {
    private Movie validMovie;

    @Test
    public void testValidateMovieWithValidMovie() {
        validMovie = TestHelper.generateMovie();
        assertTrue(MovieServiceImpl.validateMovieCreation(validMovie));
    }

    @Test
    public void testValidateMovieWithNullMovie() {
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(null));
    }

    @Test
    public void testValidateMovieWithBlankTitle() {
        validMovie = TestHelper.generateMovie();
        validMovie.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
    }

    @Test
    public void testValidateMovieWithTooBigTitle() {
        validMovie = TestHelper.generateMovie();
        validMovie.setTitle(generateStringBySize(Constants.MAX_TITLE_LENGTH + 1));
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
    }

    @Test
    public void testValidateMovieWithBlankGenre() {
        validMovie = TestHelper.generateMovie();
        validMovie.setGenre("");
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
    }

    @Test
    public void testValidateMovieWithTooLongGenre() {
        validMovie = TestHelper.generateMovie();
        validMovie.setGenre(generateStringBySize(Constants.MAX_GENRE_LENGTH + 1));
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
    }

    @Test
    public void testValidateMovieWithInvalidReleaseYearFormat() {
        validMovie = TestHelper.generateMovie();
        validMovie.setReleaseYear(null);
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
        assertTrue(MovieServiceImpl.validateMovieUpdate(validMovie));
    }

    @Test
    public void testValidateMovieWithTooOldReleaseYear() {
        validMovie = TestHelper.generateMovie();
        validMovie.setReleaseYear(Constants.MIN_MOVIE_RELEASE_YEAR - 1);
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieUpdate(validMovie));
    }

    @Test
    public void testValidateMovieWithTooRecentReleaseYear() {
        validMovie = TestHelper.generateMovie();
        validMovie.setReleaseYear(Constants.MAX_MOVIE_RELEASE_YEAR + 1);
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieUpdate(validMovie));
    }

    @Test
    public void testValidateMovieWithNullDirectorId() {
        validMovie = TestHelper.generateMovie();
        validMovie.setDirectorId(null);
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
        assertTrue(MovieServiceImpl.validateMovieUpdate(validMovie));
    }

    @Test
    public void testValidateMovieWithEmptyActorIds() {
        validMovie = TestHelper.generateMovie();
        validMovie.setActorIds(List.of());
        assertThrows(IllegalArgumentException.class, () -> MovieServiceImpl.validateMovieCreation(validMovie));
    }

}
