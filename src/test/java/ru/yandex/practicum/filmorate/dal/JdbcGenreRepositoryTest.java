package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.utils.TestDataFactory.*;

@JdbcTest
@Import({JdbcGenreRepository.class, GenreRowMapper.class, JdbcDirectorRepository.class, DirectorRowMapper.class,
        JdbcFilmRepository.class, FilmRowMapper.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcGenreRepository")
class JdbcGenreRepositoryTest {

    private final JdbcGenreRepository genreRepository;

    private final JdbcFilmRepository filmRepository;

    @Test
    @DisplayName("должен возвращать жанры фильма")
    void should_return_film_genres() {

        filmRepository.save(TEST_FILM);

        genreRepository.getFilmGenres(TEST_FILM_ID);

        Collection<Genre> genres = genreRepository.getFilmGenres(TEST_FILM_ID);

        assertThat(genres)
                .isNotEmpty()
                .extracting(Genre::getId)
                .contains(TEST_GENRE_ID);
    }

    @Test
    @DisplayName("должен находить жанр по идентификатору")
    void should_find_genre_by_id() {
        genreRepository.findById(TEST_GENRE_ID);

        Optional<Genre> genre = genreRepository.findById(TEST_GENRE_ID);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g -> assertThat(g.getId()).isEqualTo(TEST_GENRE_ID));
    }

    @Test
    @DisplayName("должен возвращать все значения жанров")
    void should_return_all_genre_values() {
        genreRepository.values();
        Collection<Genre> genres = genreRepository.values();

        assertThat(genres)
                .isNotEmpty()
                .hasSize(6)
                .extracting(Genre::getId)
                .contains(TOTAL_GENRES);
    }

    @Test
    @DisplayName("должен возвращать все идентификаторы жанров")
    void should_return_all_genre_ids() {
        genreRepository.getAllGenreIds();
        Collection<Integer> genreIds = genreRepository.getAllGenreIds();

        assertThat(genreIds)
                .isNotEmpty()
                .contains(TOTAL_GENRES);
    }
}