package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<T> {
    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;
    private final Class<T> entityType;

    protected Optional<T> findOne(String query, MapSqlParameterSource params) {
        try {
            T result = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, MapSqlParameterSource params) {
        return jdbc.query(query, params,  mapper);
    }

    public boolean delete(String query, MapSqlParameterSource params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected Long insert(String query, MapSqlParameterSource params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(query, params, keyHolder, new String[]{"id"});

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected void update(String query, MapSqlParameterSource params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected Collection<T> select(String query, MapSqlParameterSource params) {
        List<T> list = new ArrayList<>();

        jdbc.query(query, params, (ResultSet rs, int rowNum) -> {
            T mapped = mapper.mapRow(rs, rowNum);
            list.add(mapped);
            return null;
        });
        return list;
    }
}
