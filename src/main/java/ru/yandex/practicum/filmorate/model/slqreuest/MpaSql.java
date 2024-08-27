package ru.yandex.practicum.filmorate.model.slqreuest;

public class MpaSql {
    public static final String ALL_MPA_QUERY = "SELECT f.id AS film_id, f.rating_id AS id, r.name FROM films f JOIN rating r ON f.rating_id = r.id";
}
