package ru.yandex.practicum.filmorate.model.slqreuest;

public class DirectorSql {
    public static final String GET_ALL_DIRECTORS = "SELECT * FROM directors";
    public static final String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";
    public static final String INSERT_QUERY = "INSERT INTO directors (name) " + "VALUES (:name)";
    public static final String UPDATE_QUERY = "UPDATE directors SET name = :name WHERE id = :id";
    public static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = :id";
}
