package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Component("FilmRowMapper")
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    protected final NamedParameterJdbcTemplate jdbc;

    protected final RowMapper<Genre> genreMapper;

    protected final RowMapper<Mpa> mpaMapper;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setGenres(getFilmGenres(film.getId()));

        if (resultSet.getInt("rating_id") != 0) {
            film.setMpa(findMpaById(resultSet.getInt("rating_id"))
                    .orElseThrow(() -> new NotFoundException("Рейтинг не найден")));
        }


        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toInstant());

        return film;
    }


    private LinkedHashSet<Genre> getFilmGenres(Long filmId) {

        String query = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id FROM film_genres WHERE film_id = :film_id)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);

        return new LinkedHashSet<>(jdbc.query(query, params, genreMapper));
    }

    public Optional<Mpa> findMpaById(Integer id) {
        String query = "SELECT * FROM rating WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        Mpa mpa = jdbc.queryForObject(query, params, mpaMapper);
        return Optional.ofNullable(mpa);
    }

}
