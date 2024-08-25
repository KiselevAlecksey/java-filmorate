INSERT INTO rating (name) VALUES ('PG-13');

INSERT INTO genre (name) VALUES ('genre1'), ('genre2');

SELECT * FROM users;

INSERT INTO users (email, login, name, birthday)
VALUES ('email@email.com', 'login', 'name', '2024-05-02');

INSERT INTO films (name, description, duration, release_date, rating_id)
VALUES ('name', 'description', 100, '2024-05-02 00:00:00+00', 1);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1);
