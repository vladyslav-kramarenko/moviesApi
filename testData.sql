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
    ('Christopher', 'Nolan', '1970-07-30'),
    ('Quentin', 'Tarantino', '1963-03-27'),
    ('Brad', 'Pitt', '1963-12-18'),
    ('Tom', 'Hanks', '1956-07-09'),
    ('Anthony', 'Hopkins', '1937-12-31'),
    ('Jodie', 'Foster', '1962-11-19'),
    ('Martin', 'Scorsese', '1942-11-17'),
    ('Tom', 'Hanks', '1956-07-09'),
    ('Leonardo', 'DiCaprio', '1974-11-11'),
    ('Brad', 'Pitt', '1963-12-18'),
    ('Angelina', 'Jolie', '1975-06-04'),
    ('Charlize', 'Theron', '1975-08-07'),
    ('Denzel', 'Washington', '1954-12-28'),
    ('Kate', 'Winslet', '1975-10-05');

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
    ('The Silence of the Lambs', 'Thriller', '1991', 6),
    ('The Godfather: Part II', 'Crime', '1974', 2),
    ('The Green Mile', 'Drama', '1999', 1),
    ('The Revenant', 'Adventure', '2015', 3),
    ('Fight Club', 'Drama', '1999', 4),
    ('Gladiator', 'Action', '2000', 3),
    ('The Aviator', 'Drama', '2004', 3),
    ('The Prestige', 'Drama', '2006', 7),
    ('Inception', 'Action', '2010', 7),
    ('The Dark Knight Rises', 'Action', '2012', 7),
    ('The Great Gatsby', 'Drama', '2013', 4),
    ('The Wolf of Wall Street', 'Comedy', '2013', 2),
    ('Gravity', 'Science Fiction', '2013', 8),
    ('12 Years a Slave', 'Drama', '2013', 9),
    ('Fury', 'War', '2014', 3),
    ('The Martian', 'Science Fiction', '2015', 1),
    ('Mad Max: Fury Road', 'Action', '2015', 10),
    ('The Hateful Eight', 'Western', '2015', 4),
    ('Arrival', 'Science Fiction', '2016', 11),
    ('Dunkirk', 'War', '2017', 7),
    ('Blade Runner 2049', 'Science Fiction', '2017', 7),
    ('Three Billboards Outside Ebbing, Missouri', 'Crime', '2017', 12),
    ('Once Upon a Time in Hollywood', 'Comedy', '2019', 2),
    ('1917', 'War', '2019', 13);

INSERT INTO `review` (`text`, `rating`, `dateTime`, `movie_id`)
VALUES
    ('A cinematic masterpiece!', 9.7, NOW(), 1),
    ('Heartbreaking and uplifting', 8.9, NOW(), 2),
    ('Stunning cinematography and performance', 9.1, NOW(), 3),
    ('Mind-bending plot twist', 8.8, NOW(), 4),
    ('Epic and emotional', 9.3, NOW(), 5),
    ('Masterful biopic', 8.6, NOW(), 6),
    ('A mesmerizing magic act', 8.7, NOW(), 7),
    ('A mind-bending heist film', 9.0, NOW(), 8),
    ('Epic conclusion to a legendary trilogy', 9.2, NOW(), 9),
    ('A modern take on a classic novel', 8.4, NOW(), 10),
    ('A wild ride through Wall Street', 8.5, NOW(), 11),
    ('A breathtaking space adventure', 8.8, NOW(), 12),
    ('An emotional and impactful true story', 9.0, NOW(), 13),
    ('A gripping war film', 8.7, NOW(), 14),
    ('A thrilling survival story', 8.9, NOW(), 15),
    ('A high-octane action epic', 8.6, NOW(), 16),
    ('A riveting mystery western', 8.8, NOW(), 17),
    ('A thought-provoking sci-fi film', 9.1, NOW(), 18),
    ('A heart-pounding war epic', 9.4, NOW(), 19),
    ('A visually stunning sci-fi sequel', 8.9, NOW(), 20),
    ('A dark and powerful drama', 8.5, NOW(), 21),
    ('A nostalgic love letter to Hollywood', 8.7, NOW(), 22),
    ('A harrowing and intense war drama', 9.0, NOW(), 23);

INSERT INTO actor_ids (actor_id, movie_id)
VALUES
    (1, 2),
    (1, 8),
    (1, 15),
    (1, 16),
    (2, 11),
    (2, 21),
    (3, 2),
    (3, 12),
    (3, 14),
    (4, 3),
    (4, 19),
    (4, 20),
    (5, 10),
    (5, 21),
    (6, 7),
    (6, 8),
    (6, 10),
    (6, 14),
    (6, 15),
    (7, 2),
    (7, 6),
    (7, 7),
    (7, 8),
    (7, 9),
    (7, 18),
    (8, 3),
    (8, 13),
    (8, 14),
    (9, 13),
    (10, 16),
    (11, 19),
    (12, 22),
    (12, 23),
    (13, 21),
    (13, 22),
    (13, 23),
    (13, 5);

