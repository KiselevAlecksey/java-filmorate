package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.DirectorSql.DELETE_DIRECTOR_QUERY;

@Repository("JdbcDirectorRepository")
public class JdbcDirectorRepository extends BaseRepository<Director> implements DirectorRepository {
    private static final String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";

    public JdbcDirectorRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper, Director.class);
    }

    @Override
    public List<Integer> getAllIds() {
        String query = "SELECT * FROM directors";
        return jdbc.query(query, (rs, rowNum) -> rs.getInt("id"));
    }

    @Override
    public Optional<Director> getById(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return findOne(GET_DIRECTOR_BY_ID, params);
    }

    @Override
    public Director create(Director director) {

        String query = "INSERT INTO directors (name) VALUES (:name)";

        Long id = insert(query, new MapSqlParameterSource()
                .addValue("name", director.getName()));

        return getById(id).orElseThrow(
                () -> new NotFoundException("Не удалось добавить режиссёра")
        );
    }

    @Override
    public Director update(Director director) {

        String queryUpdate = "UPDATE directors SET name = :name WHERE id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", director.getId());
        params.put("name", director.getName());

        update(queryUpdate, params);

        return getById(director.getId()).orElseThrow(
                () -> new NotFoundException("Не удалось обновить режиссёра")
        );
    }

    @Override
    public Collection<Director> values() {
        return jdbc.query("SELECT * FROM directors", mapper);
    }

    @Override
    public boolean delete(Long id) {
       /* String query = "DELETE FROM directors (name) VALUES (:name)";
        insert(query, new MapSqlParameterSource().addValue("name", director.getName()));*/

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        Optional<Director> optionalDirector = getById(id);

        if (optionalDirector.isEmpty()) {
            return false;
        }

        return delete(DELETE_DIRECTOR_QUERY, params);
    }

    @Override
    public List<Director> getByIds(List<Long> ids) {

        String query = "SELECT id, name FROM directors WHERE id IN (:ids)";

        return jdbc.query(query, new MapSqlParameterSource("ids", ids), mapper);
    }
}