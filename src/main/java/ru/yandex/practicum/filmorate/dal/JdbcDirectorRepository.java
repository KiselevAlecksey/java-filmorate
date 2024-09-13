package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.interfaces.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.DirectorSql.*;
import static ru.yandex.practicum.filmorate.utils.ModelConverter.mapToDirectorDto;

@Repository("JdbcDirectorRepository")
public class JdbcDirectorRepository extends BaseRepository<Director> implements DirectorRepository {

    public JdbcDirectorRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper, Director.class);
    }

    @Override
    public List<Long> getAllIds() {
        return jdbc.query(GET_ALL_DIRECTORS, (rs, rowNum) -> rs.getLong("id"));
    }

    @Override
    public Optional<Director> getById(Long id) {
        return findOne(GET_DIRECTOR_BY_ID, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Director create(Director director) {

        Long id = insert(INSERT_QUERY, new MapSqlParameterSource()
                .addValue("name", director.getName()));

        return getById(id).orElseThrow(
                () -> new NotFoundException("Не удалось добавить режиссёра")
        );
    }

    @Override
    public Director update(Director director) {

        update(UPDATE_QUERY, new MapSqlParameterSource().addValue("id", director.getId()).addValue("name", director.getName()));

        return getById(director.getId()).orElseThrow(
                () -> new NotFoundException("Не удалось обновить режиссёра")
        );
    }

    @Override
    public Collection<Director> values() {
        return jdbc.query(GET_ALL_DIRECTORS, mapper);
    }

    @Override
    public boolean delete(Long id) {

        Optional<Director> optionalDirector = getById(id);

        if (optionalDirector.isEmpty()) {
            return false;
        }

        return delete(DELETE_DIRECTOR_QUERY, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public List<DirectorDto> getByIds(List<Long> ids) {

        List<Director> directors = jdbc.query(GET_BY_IDS, new MapSqlParameterSource("ids", ids), mapper);

        LinkedHashSet<DirectorDto> directorDto = mapToDirectorDto(new LinkedHashSet<>(directors));

        return new ArrayList<>(directorDto);
    }
}
