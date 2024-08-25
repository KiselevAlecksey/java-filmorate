package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("JdbcGenreRepository")
public class JdbcGenreRepository extends BaseRepository implements GenreRepository {
    private static final String FIND_BY_ID_GENRE = "SELECT * FROM genre WHERE id = ?";

    public JdbcGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    @Override
    public void save(Genre genre) {
        String query = "INSERT INTO genre (name) VALUES (?)";
        insert(query, genre.getName());
    }

    @Override
    public void update(Genre genre) {
    }

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        return findMany("SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genres WHERE film_id = ?)", filmId);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return findOne(FIND_BY_ID_GENRE, id);
    }

    @Override
    public Collection<Genre> values() {
        return findMany("SELECT * FROM genre");
    }

    @Override
    public List<Integer> getAllGenreIds() {
        String query = "SELECT id FROM genre";
        return jdbc.query(query, (rs, rowNum) -> rs.getInt("id"));
    }
}
