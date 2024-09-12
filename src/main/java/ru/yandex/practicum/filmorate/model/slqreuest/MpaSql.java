package ru.yandex.practicum.filmorate.model.slqreuest;

public class MpaSql {
    public static final String ALL_MPA_QUERY = "\n" +
            "   SELECT f.id AS film_id, f.rating_id AS id, r.name \n" +
            "   FROM films f JOIN rating r ON f.rating_id = r.id";
    public static final String GET_BY_ID = "SELECT * FROM rating WHERE id = :id";
    public static final String GET_ALL = "SELECT * FROM rating";

    public static final String GET_ALL_IDS = "SELECT id FROM rating";
}
