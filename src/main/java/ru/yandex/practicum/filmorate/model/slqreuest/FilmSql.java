package ru.yandex.practicum.filmorate.model.slqreuest;

public class FilmSql {

    public static final String DELETE_GENRE = "DELETE FROM film_genres WHERE film_id = :film_id";
    public static final String UPDATE_GENRE = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (:film_id, :genre_id)";
    public static final String ALL_FILMS_QUERY = "SELECT id AS film_id, name AS film_name, " +
            "description AS film_description, duration AS film_duration, " +
            "release_date AS film_release_date, rating_id AS rating_id " +
            "FROM films;";

    public static final String SEARCH_QUERY = "SELECT f.*, " +
            "COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN directors d ON fd.director_id = d.id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "LEFT JOIN likes l ON f.id = l.film_id ";

    public static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, rating_id)" +
            " VALUES (:name, :description, :release_date, :duration, :rating_id)";
    public static final String UPDATE_FILM = "UPDATE films SET name = :name, description = :description," +
            " duration = :duration, release_date = :release_date, rating_id = :rating_id WHERE id = :id";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = :id";
    public static final String DELETE_FILM = "DELETE FROM films WHERE id = :id";
    public static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (:film_id, :user_id)";
    public static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id";
    public static final String GET_POPULAR_FILMS_QUERY = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC";
    public static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (:film_id, :genre_id)";

    public static final String FIND_FILM_BY_ID_GENRE = "SELECT * FROM films " +
            "WHERE id IN (SELECT film_id AS id FROM film_genres WHERE genre_id IN (:genre_id))";

    public static final String FIND_FILMS_BY_ID = "SELECT * FROM films WHERE id = :id";

    public static final String GET_RECOMMENDED_FILMS = "SELECT films.*, r.* " +
            "FROM films " +
            "JOIN rating AS r ON r.id = films.rating_id " +
            "WHERE films.id IN ( " +
            "SELECT DISTINCT film_id " +
            "FROM likes " +
            "WHERE user_id IN ( " +
            "SELECT user_id " +
            "FROM ( " +
            "SELECT user_id, COUNT(*) AS matches " +
            "FROM likes " +
            "WHERE user_id <> :user_id " +
            "AND film_id IN ( " +
            "SELECT film_id " +
            "FROM likes " +
            "WHERE user_id = :user_id " +
            ") " +
            "GROUP BY user_id " +
            "ORDER BY matches DESC " +
            ") AS matched_users " +
            "GROUP BY user_id " +
            "HAVING matches = MAX(matches) " +
            ") " +
            "AND film_id NOT IN ( " +
            "SELECT film_id " +
            "FROM likes " +
            "WHERE user_id = :user_id " +
            ") " +
            ");";

    public static final String GET_COMMON_FILMS = "SELECT f.*, COUNT(allLikes.film_id) AS like_count " +
            "FROM films AS f " +
            "INNER JOIN likes AS userLikes ON f.id = userLikes.film_id " +
            "INNER JOIN likes AS friendLikes ON f.id = friendLikes.film_id " +
            "LEFT JOIN likes AS allLikes ON f.id = allLikes.film_id " +
            "WHERE userLikes.user_id = :user_id " +
            "AND friendLikes.user_id = :friend_id " +
            "GROUP BY f.id " +
            "ORDER BY like_count DESC, f.id; ";

    public static final String USER_LIKE_COUNT_IN_LIKES = "\n" +
            "SELECT COUNT(*) AS total_likes\n" +
            "FROM likes\n" +
            "WHERE film_id = :film_id AND user_id = :user_id;";

    public static final String DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = :film_id";
    public static final String INSERT_GENRES = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (:film_id, :genre_id)";
    public static final String DELETE_DIRECTORS = "DELETE FROM film_directors WHERE film_id = :film_id";
    public static final String INSERT_DIRECTORS = "INSERT INTO film_directors (director_id, film_id) " +
            "VALUES (:director_id, :film_id)";
}
