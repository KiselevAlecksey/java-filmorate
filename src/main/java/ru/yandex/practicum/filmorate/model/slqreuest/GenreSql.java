package ru.yandex.practicum.filmorate.model.slqreuest;

public class GenreSql {
    public static final String ALL_GENRE_QUERY = "SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name " +
            "FROM film_genres fg " +
            "JOIN genre g ON fg.genre_id = g.id;";
}
