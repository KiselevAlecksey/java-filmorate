package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class GenreRowMapper implements RowMapper<Genre> {
    protected final NamedParameterJdbcTemplate jdbc;

    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }
}
