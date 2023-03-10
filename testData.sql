USE `movies`;

INSERT INTO `person` (`firstName`, `lastName`, `birthDate`)
VALUES
    ('John', 'Doe', '1990-01-01'),
    ('Jane', 'Doe', '1995-05-05');

INSERT INTO `movie` (`title`, `genre`, release_year, director_id)
VALUES
    ('The Shawshank Redemption', 'Drama', '1994', 1),
    ('The Godfather', 'Crime', '1972', 1),
    ('The Dark Knight', 'Action', '2008', 1),
    ('Pulp Fiction', 'Crime', '1994', 1);

INSERT INTO `review` (`text`, `rating`, `dateTime`, `movie_id`)
VALUES
    ('Amazing movie!', 9.5, NOW(), 1),
    ('Great acting and storyline', 8.8, NOW(), 2),
    ('One of the best superhero movies ever', 9.0, NOW(), 3),
    ('Classic Tarantino', 8.5, NOW(), 4);

INSERT INTO actor_ids (actor_id, movie_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (2, 4);