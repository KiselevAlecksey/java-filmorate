package ru.yandex.practicum.filmorate.model;

public class FilmSql {

    public static final String DELETE_GENRE = "DELETE FROM film_genres WHERE film_id = ?";
    public static final String UPDATE_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    public static final String FIND_ALL_FILMS = "SELECT \n" +
            "    f.id AS film_id,\n" +
            "    f.name AS film_name,\n" +
            "    f.description AS film_description,\n" +
            "    f.duration AS film_duration,\n" +
            "    f.release_date AS film_release_date,\n" +
            "    r.id AS rating_id,\n" +
            "    r.name AS rating_name,\n" +
            "    g.id AS genre_id,\n" +
            "    g.name AS genre_name\n" +
            "FROM films f\n" +
            "LEFT JOIN rating r ON f.rating_id = r.id\n" +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id\n" +
            "LEFT JOIN genre g ON fg.genre_id = g.id;";

    public static final String INSERT_FILM = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?," +
            " duration = ?, release_date = ?, rating_id = ? WHERE id = ?";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    public static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    public static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    public static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    public static final String GET_POPULAR_FILMS_QUERY = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";
    public static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    public static final String FIND_FILM_BY_ID_GENRE = "SELECT * FROM films " +
            "WHERE id IN (SELECT film_id AS id FROM film_genres WHERE genre_id IN (?))";

    public static final String FIND_FILMS_BY_ID = "SELECT * FROM films WHERE id = ?";
}
