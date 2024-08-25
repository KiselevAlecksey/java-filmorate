-- Наполнение таблицы rating

MERGE INTO rating AS target
USING (SELECT 1 AS id, 'G' AS name UNION ALL
       SELECT 2, 'PG' UNION ALL
       SELECT 3, 'PG-13' UNION ALL
       SELECT 4, 'R' UNION ALL
       SELECT 5, 'NC-17') AS source
ON target.id = source.id
WHEN NOT MATCHED THEN
    INSERT (id, name) VALUES (source.id, source.name);

-- Наполнение таблицы genre
MERGE INTO genre AS target
USING (SELECT 1 AS id, 'Комедия' AS name UNION ALL
       SELECT 2, 'Драма' UNION ALL
       SELECT 3, 'Мультфильм' UNION ALL
       SELECT 4, 'Триллер' UNION ALL
       SELECT 5, 'Документальный' UNION ALL
       SELECT 6, 'Боевик') AS source
ON target.id = source.id
WHEN NOT MATCHED THEN
    INSERT (id, name) VALUES (source.id, source.name);

/*-- Наполнение таблицы users
MERGE INTO users AS target
USING (SELECT 1 AS id, 'user1@example.com' AS email, 'user1' AS login, 'Алексей' AS name, '1990-01-01 10:00:00+00' AS birthday UNION ALL
       SELECT 2, 'user2@example.com', 'user2', 'Мария', '1992-02-15 10:00:00+00' UNION ALL
       SELECT 3, 'user3@example.com', 'user3', 'Дмитрий', '1985-03-20 10:00:00+00' UNION ALL
       SELECT 4, 'user4@example.com', 'user4', 'Елена', '1988-04-25 10:00:00+00') AS source
ON target.id = source.id
WHEN NOT MATCHED THEN
    INSERT (id, email, login, name, birthday) VALUES (source.id, source.email, source.login, source.name, source.birthday);

-- Наполнение таблицы films
MERGE INTO films AS target
USING (SELECT 1 AS id, 'Фильм 1' AS name, 'Описание фильма 1' AS description, 120 AS duration, '2020-01-01 10:00:00+00' AS release_date, 4 AS rating_id UNION ALL
       SELECT 2, 'Фильм 2', 'Описание фильма 2', 150, '2019-05-05 10:00:00+00', 3 UNION ALL
       SELECT 3, 'Фильм 3', 'Описание фильма 3', 90, '2018-10-10 10:00:00+00', 2 UNION ALL
       SELECT 4, 'Фильм 4', 'Описание фильма 4', 180, '2021-03-15 10:00:00+00', 1) AS source
ON target.id = source.id
WHEN NOT MATCHED THEN
    INSERT (id, name, description, duration, release_date, rating_id)
    VALUES (source.id, source.name, source.description, source.duration, source.release_date, source.rating_id);

-- Наполнение таблицы friends
MERGE INTO friends AS target
USING (SELECT 1 AS user_id, 2 AS friend_id UNION ALL
       SELECT 1, 3 UNION ALL
       SELECT 2, 4 UNION ALL
       SELECT 3, 1) AS source
ON target.user_id = source.user_id AND target.friend_id = source.friend_id
WHEN NOT MATCHED THEN
    INSERT (user_id, friend_id) VALUES (source.user_id, source.friend_id);

-- Наполнение таблицы film_genre
MERGE INTO film_genres AS target
USING (SELECT 1 AS film_id, 1 AS genre_id UNION ALL
       SELECT 1, 3 UNION ALL
       SELECT 2, 2 UNION ALL
       SELECT 3, 4 UNION ALL
       SELECT 4, 5) AS source
ON target.film_id = source.film_id AND target.genre_id = source.genre_id
WHEN NOT MATCHED THEN
    INSERT (film_id, genre_id) VALUES (source.film_id, source.genre_id);

-- Наполнение таблицы likes
MERGE INTO likes AS target
USING (SELECT 1 AS film_id, 1 AS user_id UNION ALL
       SELECT 1, 2 UNION ALL
       SELECT 2, 3 UNION ALL
       SELECT 3, 1 UNION ALL
       SELECT 4, 4) AS source
ON target.film_id = source.film_id AND target.user_id = source.user_id
WHEN NOT MATCHED THEN
    INSERT (film_id, user_id) VALUES (source.film_id, source.user_id);*/
