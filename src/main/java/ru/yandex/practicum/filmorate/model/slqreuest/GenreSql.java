package ru.yandex.practicum.filmorate.model.slqreuest;

public class GenreSql {
    public static final String ALL_GENRE_QUERY = "SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name " +
            "FROM film_genres fg " +
            "JOIN genre g ON fg.genre_id = g.id;";
    public static final String FIND_BY_ID_GENRE = "SELECT * FROM genre WHERE id = :id";

    public static final String GET_GENRES_BY_FILM = "SELECT * FROM genre " +
            "WHERE id IN (SELECT genre_id AS id FROM film_genres WHERE film_id = :film_id)";

    public static final String GET_BY_IDS_QUERY = "SELECT id, name FROM genre WHERE id IN (:ids)";

    public static final String GET_ALL_IDS = "SELECT id FROM genre";

    public static final String GET_ALL = "SELECT * FROM genre";

    public static final String INSERT_GENRE = "INSERT INTO genre (name) VALUES (:name)";
}
