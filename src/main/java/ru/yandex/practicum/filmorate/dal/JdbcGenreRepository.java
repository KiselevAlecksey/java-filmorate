package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository("JdbcGenreRepository")
public class JdbcGenreRepository extends BaseRepository implements GenreRepository {
    private static final String FIND_BY_ID_GENRE = "SELECT * FROM genre WHERE id = :id";

    public JdbcGenreRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    @Override
    public void save(Genre genre) {
        String query = "INSERT INTO genre (name) VALUES (:name)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", genre.getName());

        insert(query, params);
    }

    @Override
    public void update(Genre genre) {
    }

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        String query = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id AS id FROM film_genres WHERE film_id = :film_id)";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);

        Collection<Genre> genres = findMany(query, params);
        return genres;
    }

    @Override
    public Optional<Genre> findById(Integer id) {

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);

        return findOne(FIND_BY_ID_GENRE, params);
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
}
