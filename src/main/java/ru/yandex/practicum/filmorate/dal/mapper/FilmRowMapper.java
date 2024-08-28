package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
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
        film.setGenres(new LinkedHashSet<>());

        Mpa mpa = new Mpa();

        if (resultSet.getInt("rating_id") != 0) {
            mpa.setId(resultSet.getInt("rating_id"));
            film.setMpa(mpa);
        }

        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toInstant());

        return film;
    }

}
