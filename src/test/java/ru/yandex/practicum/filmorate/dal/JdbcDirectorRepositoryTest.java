package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.dal.interfaces.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.mapper.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.utils.TestDataFactory.*;

@JdbcTest
@Import({JdbcDirectorRepository.class, DirectorRowMapper.class,
        GenreRowMapper.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository")
public class JdbcDirectorRepositoryTest {

    private final DirectorRepository directorRepository;

    private final NamedParameterJdbcTemplate jdbc;

    @BeforeEach
    void init() {
        jdbc.update("DELETE FROM directors", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE directors ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        TEST_DIRECTOR.setId(TEST_DIRECTOR_ID);
    }

    @Test
    @DisplayName("Должен возвращать все id режиссёров")
    void should_get_all_ids_directors() {

        TEST_DIRECTORS.forEach(directorRepository::create);

        List<Long> directors = directorRepository.getAllIds();

        assertThat(directors)
                .isNotEmpty()
                .contains(TEST_DIRECTOR_ID, TEST_DIRECTOR_ID2);
    }

    @Test
    @DisplayName("Должен создать режиссёра")
    void should_create_director() {

        Director director = directorRepository.create(TEST_DIRECTOR);

        assertThat(director)
                .usingRecursiveComparison()
                .isEqualTo(TEST_DIRECTOR);
    }

    @Test
    @DisplayName("Должен обновлять поля режиссёра")
    void should_update_director() {

        Director director = TEST_DIRECTOR;

        director.setName("Update director");
        director.setId(TEST_DIRECTOR_ID2);

        directorRepository.create(director);

        assertThat(director)
                .usingRecursiveComparison()
                .isEqualTo(TEST_DIRECTOR);
    }

    @Test
    @DisplayName("Должен возвращать всех режиссёров")
    void should_get_all_directors() {
        TEST_DIRECTORS.forEach(directorRepository::create);

        Collection<Director> directors = directorRepository.values();

        assertThat(directors)
                .isNotEmpty()
                .hasSize(TEST_DIRECTORS.size())
                .containsExactlyInAnyOrderElementsOf(TEST_DIRECTORS);
    }

    @Test
    @DisplayName("Должен возвращать режиссёров по списку id")
    void should_get_directors_by_ids() {
        TEST_DIRECTORS.forEach(directorRepository::create);

        List<DirectorDto> directors = directorRepository.getByIds(List.of(TEST_DIRECTOR_ID, TEST_DIRECTOR_ID2));

        assertThat(directors)
                .hasSize(TEST_COUNT_TWO)
                .extracting(DirectorDto::getId)
                .containsExactlyInAnyOrder(TEST_DIRECTOR_ID, TEST_DIRECTOR_ID2);
    }
}
