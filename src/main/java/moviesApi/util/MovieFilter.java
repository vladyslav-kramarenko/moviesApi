package moviesApi.util;

import moviesApi.domain.Movie;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class MovieFilter {

    private String title;
    private String genre;
    private Integer year;
    private Long directorId;
    private Long[] actorIds;

    public static MovieFilterBuilder builder() {
        return new MovieFilterBuilder();
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getYear() {
        return year;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public Long[] getActorIds() {
        return actorIds;
    }

    public static class MovieFilterBuilder {
        private String title;
        private String genre;
        private Integer year;
        private Long directorId;
        private Long[] actorIds;

        public MovieFilterBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public MovieFilterBuilder withGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public MovieFilterBuilder withYear(Integer year) {
            this.year = year;
            return this;
        }

        public MovieFilterBuilder withDirectorId(Long directorId) {
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
        this.genre = builder.genre;
        this.year = builder.year;
        this.directorId = builder.directorId;
        this.actorIds = builder.actorIds;
    }

    public Stream<Movie> filter(Stream<Movie> input) {
        return input
                .filter(movie -> (title == null || movie.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(movie -> (genre == null || movie.getGenre().equalsIgnoreCase(genre)))
                .filter(movie -> (year == null || movie.getReleaseYear().equals(year)))
                .filter(movie -> (directorId == null || movie.getDirectorId().equals(directorId)))
                .filter(movie -> (actorIds == null || movie.getActorIds().containsAll(new HashSet<>(Arrays.asList(actorIds)))));
    }
}
