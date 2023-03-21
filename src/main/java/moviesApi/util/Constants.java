package moviesApi.util;

/**
 * This class contains constant values used throughout the application.
 */
public class Constants {
    public static final int MAX_FIRST_NAME_LENGTH = 64;
    public static final int MAX_LAST_NAME_LENGTH = 64;
    public static final int MAX_TITLE_LENGTH = 255;
    public static final int MIN_MOVIE_RELEASE_YEAR = 1895;
    public static final int MAX_MOVIE_RELEASE_YEAR = 9999;
    public static final int MIN_REVIEW_RATING = 1;
    public static final int MAX_GENRE_LENGTH = 45;
    public static final int MAX_REVIEW_RATING = 10;
    public static final int MAX_REVIEW_LENGTH = 255;

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT = "id,asc";

    public static final String[] ALLOWED_REVIEW_SORT_PROPERTIES = {"id", "dateTime", "rating"};
    public static final String[] ALLOWED_MOVIE_SORT_PROPERTIES = {"id", "title", "releaseYear", "genre", "directorId"};
}
