package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Repository("JdbcMpaRepository")
public class JdbcMpaRepository extends BaseRepository<Mpa> implements MpaRepository {

    public JdbcMpaRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    @Override
    public Mpa save(Mpa mpa) {
        return null;
    }

    @Override
    public Mpa update(Mpa mpa) {
        return null;
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        String query = "SELECT * FROM rating WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        Optional<Mpa> mpa = findOne(query, params);
        return mpa;
    }

    @Override
    public Collection<Mpa> values() {
        String query = "SELECT * FROM rating";
        return jdbc.query(query, mapper);
    }

    @Override
    public List<Integer> getAllMpaIds() {
        String query = "SELECT id FROM rating";
        return jdbc.query(query, (rs, rowNumber) -> rs.getInt("id"));
    }
}
