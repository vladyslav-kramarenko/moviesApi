package moviesApi.filter;

import moviesApi.domain.Movie;
import moviesApi.util.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class MovieFilter {

    private String title;
    private String[] genres;
    private Integer year;
    private Integer fromYear;
    private Integer toYear;
    private Long directorId;
    private Long[] actorIds;

    public static MovieFilterBuilder builder() {
        return new MovieFilterBuilder();
    }

    public String getTitle() {
        return title;
    }

    public String[] getGenres() {
        return genres;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getToYear() {
        return toYear;
    }

    public Integer getFromYear() {
        return fromYear;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public Long[] getActorIds() {
        return actorIds;
    }

    public static class MovieFilterBuilder {
        private String title;
        private String[] genres;
        private Integer year;
        private Integer fromYear;
        private Integer toYear;
        private Long directorId;
        private Long[] actorIds;

        public MovieFilterBuilder withTitle(String title) {
            if (title != null && title.length() > Constants.MAX_TITLE_LENGTH) {
                throw new IllegalArgumentException("First Name must be under " + Constants.MAX_FIRST_NAME_LENGTH + " characters");
            }
            this.title = title;
            return this;
        }

        public MovieFilterBuilder withGenre(String[] genres) {
            this.genres = genres;
            return this;
        }

        public MovieFilterBuilder withYear(Integer year) {
            if (year != null && (year < Constants.MIN_MOVIE_RELEASE_YEAR || year > Constants.MAX_MOVIE_RELEASE_YEAR)) {
                throw new IllegalArgumentException(
                        "Release year should be bigger then " +
                                Constants.MIN_MOVIE_RELEASE_YEAR +
                                " and smaller than " +
                                Constants.MAX_MOVIE_RELEASE_YEAR);
            }
            this.year = year;
            return this;
        }

        public MovieFilterBuilder withFromYear(Integer fromYear) {
            if (fromYear != null && (fromYear > Constants.MAX_MOVIE_RELEASE_YEAR)) {
                throw new IllegalArgumentException(
                        "Release year filter should be smaller than " +
                                Constants.MAX_MOVIE_RELEASE_YEAR);
            }
            this.fromYear = fromYear;
            return this;
        }

        public MovieFilterBuilder withToYear(Integer toYear) {
            if (toYear != null && (toYear < Constants.MIN_MOVIE_RELEASE_YEAR)) {
                throw new IllegalArgumentException(
                        "Release year filter should be bigger then " +
                                Constants.MIN_MOVIE_RELEASE_YEAR);
            }
            this.toYear = toYear;
            return this;
        }

        public MovieFilterBuilder withDirectorId(Long directorId) {
            if (directorId != null && directorId < 0) {
                throw new IllegalArgumentException("director ID should be positive");
            }
            this.directorId = directorId;
            return this;
        }

        public MovieFilterBuilder withActorIds(Long[] actorIds) {
            this.actorIds = actorIds;
            return this;
        }

        public MovieFilter build() {
            return new MovieFilter(this);
        }
    }

    private MovieFilter(MovieFilterBuilder builder) {
        this.title = builder.title;
        this.genres = builder.genres;
        this.year = builder.year;
        this.fromYear = builder.fromYear;
        this.toYear = builder.toYear;
        this.directorId = builder.directorId;
        this.actorIds = builder.actorIds;
    }

    public Stream<Movie> filter(Stream<Movie> input) {
        return input
                .filter(movie -> (title == null || movie.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(movie -> (genres == null || Arrays.stream(genres).anyMatch(x -> x.equalsIgnoreCase(movie.getGenre()))))
                .filter(movie -> (fromYear == null || movie.getReleaseYear() >= fromYear))
                .filter(movie -> (toYear == null || movie.getReleaseYear() <= toYear))
                .filter(movie -> (year == null || movie.getReleaseYear().equals(year)))
                .filter(movie -> (directorId == null || movie.getDirectorId().equals(directorId)))
                .filter(movie -> (actorIds == null || new HashSet<>(movie.getActorIds()).containsAll(new HashSet<>(Arrays.asList(actorIds)))));
    }
}
