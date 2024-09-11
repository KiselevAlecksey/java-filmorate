package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.interfaces.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository("JdbcGenreRepository")
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_BY_ID_GENRE = "SELECT * FROM genre WHERE id = :id";

    public JdbcGenreRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    @Override
    public void save(Genre genre) {
        String query = "INSERT INTO genre (name) VALUES (:name)";

        insert(query, new MapSqlParameterSource().addValue("name", genre.getName()));
    }

    @Override
    public void update(Genre genre) {
    }

    @Override
    public LinkedHashSet<Genre> getFilmGenres(Long filmId) {
        String query = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id AS id FROM film_genres WHERE film_id = :film_id)";

        Collection<Genre> genres = findMany(query, new MapSqlParameterSource().addValue("film_id", filmId));
        return new LinkedHashSet<>(genres);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return findOne(FIND_BY_ID_GENRE, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Collection<Genre> values() {
        return jdbc.query("SELECT * FROM genre", mapper);
    }

    @Override
    public List<Integer> getAllGenreIds() {
        String query = "SELECT id FROM genre";
        return jdbc.query(query, (rs, rowNum) -> rs.getInt("id"));
    }

    @Override
    public List<Genre> getByIds(List<Integer> ids) {

        String query = "SELECT id, name FROM genre WHERE id IN (:ids)";

        return jdbc.query(query, new MapSqlParameterSource("ids", ids), mapper);
    }
}
