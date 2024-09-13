package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.interfaces.MpaRepository;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.MpaSql.*;
import static ru.yandex.practicum.filmorate.utils.ModelConverter.*;

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
    public Optional<MpaDto> findById(Integer id) {
        Mpa mpa = findOne(GET_BY_ID, new MapSqlParameterSource().addValue("id", id)).orElseThrow(
                () -> new NotFoundException("Рейтинг не найден")
        );
        return Optional.ofNullable(mapToMpaDto(mpa));
    }

    @Override
    public Collection<Mpa> values() {
        return jdbc.query(GET_ALL, mapper);
    }

    @Override
    public List<Integer> getAllMpaIds() {
        return jdbc.query(GET_ALL_IDS, (rs, rowNumber) -> rs.getInt("id"));
    }
}
