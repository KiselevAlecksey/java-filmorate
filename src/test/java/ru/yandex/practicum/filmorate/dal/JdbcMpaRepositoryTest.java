package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcMpaRepository.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcMpaRepository")
class JdbcMpaRepositoryTest {

    private final JdbcMpaRepository mpaRepository;

    private static final int TEST_MPA_ID = 1;
    private static final int TOTAL_MPA = 5;

    @Test
    @DisplayName("должен находить MPA по идентификатору")
    void should_find_mpa_by_id() {

        Optional<Mpa> mpa = mpaRepository.findById(TEST_MPA_ID);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m -> assertThat(m.getId()).isEqualTo(TEST_MPA_ID));
    }

    @Test
    @DisplayName("должен возвращать все значения MPA")
    void should_return_all_mpa_values() {

        Collection<Mpa> mpas = mpaRepository.values();

        assertThat(mpas)
                .isNotEmpty()
                .hasSize(TOTAL_MPA);
    }

    @Test
    @DisplayName("должен возвращать все идентификаторы MPA")
    void should_return_all_mpa_ids() {

        Collection<Integer> mpaIds = mpaRepository.getAllMpaIds();

        assertThat(mpaIds)
                .isNotEmpty()
                .contains(TEST_MPA_ID); // Замените на реальный ID ожидаемого MPA
    }
}