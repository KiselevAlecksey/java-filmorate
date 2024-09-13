package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.interfaces.GenreRepository;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.GenreSql.*;
import static ru.yandex.practicum.filmorate.utils.ModelConverter.*;

@Repository("JdbcGenreRepository")
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {

    public JdbcGenreRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    @Override
    public void save(Genre genre) {
        insert(INSERT_GENRE, new MapSqlParameterSource().addValue("name", genre.getName()));
    }

    @Override
    public void update(Genre genre) {
    }

    @Override
    public LinkedHashSet<Genre> getFilmGenres(Long filmId) {
        Collection<Genre> genres = findMany(GET_GENRES_BY_FILM, new MapSqlParameterSource().addValue("film_id", filmId));
        return new LinkedHashSet<>(genres);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return findOne(FIND_BY_ID_GENRE, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Collection<Genre> values() {
        return jdbc.query(GET_ALL, mapper);
    }

    @Override
    public List<Integer> getAllGenreIds() {
        return jdbc.query(GET_ALL_IDS, (rs, rowNum) -> rs.getInt("id"));
    }

    @Override
    public List<GenreDto> getByIds(List<Integer> ids) {
        List<Genre> genres = jdbc.query(GET_BY_IDS_QUERY, new MapSqlParameterSource("ids", ids), mapper);

        LinkedHashSet<GenreDto> genreDto = mapToGenreDto(new LinkedHashSet<>(genres));

        return new ArrayList<>(genreDto);
    }
}
