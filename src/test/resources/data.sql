MERGE INTO rating KEY (id) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

MERGE INTO genre AS target
USING (VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик')) AS source(name)
ON target.name = source.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (source.name);

INSERT INTO users (email, login, name, birthday)
VALUES ('email@email.com', 'login', 'name', '2024-05-02');

INSERT INTO films (name, description, duration, release_date, rating_id)
VALUES ('name', 'description', 100, '2024-05-02 00:00:00+00', 1);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1);
