USE `movies`;

-- Delete all data from tables
DELETE FROM `review`;
DELETE FROM `actor_ids`;
DELETE FROM `movie`;
DELETE FROM `person`;

ALTER TABLE `person` AUTO_INCREMENT = 1;
ALTER TABLE `movie` AUTO_INCREMENT = 1;
ALTER TABLE `review` AUTO_INCREMENT = 1;

-- Insert test data
INSERT INTO `person` (`firstName`, `lastName`, `birthDate`)
VALUES
    ('John', 'Doe', '1990-01-01'),
    ('Jane', 'Doe', '1995-05-05'),
    ('Robert', 'De Niro', '1943-08-17'),
    ('Al', 'Pacino', '1940-04-25'),
    ('Tim', 'Robbins', '1958-10-16'),
    ('Morgan', 'Freeman', '1937-06-01'),
    ('Christopher', 'Nolan', '1970-07-30');

INSERT INTO `movie` (`title`, `genre`, release_year, director_id)
VALUES
    ('The Shawshank Redemption', 'Drama', '1994', 1),
    ('The Godfather', 'Crime', '1972', 2),
    ('The Dark Knight', 'Action', '2008', 1),
    ('Pulp Fiction', 'Crime', '1994', 4),
    ('Goodfellas', 'Crime', '1990', 2),
    ('The Departed', 'Crime', '2006', 2),
    ('The Matrix', 'Action', '1999', 5),
    ('Forrest Gump', 'Drama', '1994', 1),
    ('The Silence of the Lambs', 'Thriller', '1991', 6);

INSERT INTO `review` (`text`, `rating`, `dateTime`, `movie_id`)
VALUES
    ('Amazing movie!', 9.5, NOW(), 1),
    ('Great acting and storyline', 8.8, NOW(), 2),
    ('One of the best superhero movies ever', 9.0, NOW(), 3),
    ('Classic Tarantino', 8.5, NOW(), 4),
    ('One of the best gangster movies of all time', 9.2, NOW(), 5),
    ('Intense and thrilling', 8.7, NOW(), 6),
    ('Revolutionary sci-fi film', 8.9, NOW(), 7),
    ('Heartwarming and inspiring', 8.1, NOW(), 8),
    ('Chilling and suspenseful', 8.6, NOW(), 9);

INSERT INTO actor_ids (actor_id, movie_id)
VALUES
    (1, 1),
    (1, 8),
    (2, 2),
    (2, 5),
    (3, 2),
    (3, 5),
    (3, 6),
    (4, 4),
    (4, 5),
    (5, 2),
    (5, 5),
    (5, 6),
    (6, 1),
    (6, 9);

