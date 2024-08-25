package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    protected final JdbcTemplate jdbc;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setGenres(getGenresForFilm(film.getId()));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("rating_id"));
        film.setMpa(mpa);

        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toInstant());

        return film;
    }

    private LinkedHashSet<Genre> getGenresForFilm(Long filmId) {

        String query = "SELECT genre_id FROM film_genres WHERE film_id = ?";

        List<Genre> genres = new ArrayList<>();

        jdbc.query(query, new Object[]{filmId}, rs -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genres.add(genre);
        });

        return new LinkedHashSet<>(genres);
    }

}
