package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("JdbcMpaRepository")
public class JdbcMpaRepository extends BaseRepository implements MpaRepository {

    public JdbcMpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
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
        String query = "SELECT * FROM rating WHERE id = ?";
        Optional<Mpa> mpa = findOne(query, id);
        return mpa;
    }

    @Override
    public Collection<Mpa> values() {
        String query = "SELECT * FROM rating";
        return findMany(query);
    }

    @Override
    public List<Integer> getAllMpaIds() {
        String query = "SELECT id FROM rating";
        return jdbc.query(query, (rs, rowNum) -> rs.getInt("id"));
    }
}
