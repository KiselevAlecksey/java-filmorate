package ru.yandex.practicum.filmorate.model.slqreuest;

public class DirectorSql {
    public static final String GET_ALL_DIRECTORS = "SELECT * FROM directors";
    public static final String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";
    public static final String INSERT_QUERY = "INSERT INTO directors (name) " + "VALUES (:name)";
    public static final String UPDATE_QUERY = "UPDATE directors SET name = :name WHERE id = :id";
    public static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = :id";

    public static final String ALL_DIRECTOR_QUERY = "SELECT fd.film_id, d.id AS director_id, d.name AS director_name " +
            "FROM film_directors fd " +
            "JOIN directors d ON fd.director_id = d.id;";
}
