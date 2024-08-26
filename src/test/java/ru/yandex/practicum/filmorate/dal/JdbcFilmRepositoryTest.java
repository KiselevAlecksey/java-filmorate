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
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@JdbcTest
@Import({JdbcFilmRepository.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository")
class JdbcFilmRepositoryTest {
    public static final long TEST_USER_ID = 1L;
    public static final long TEST_FILM_ID = 1L;

    private final JdbcFilmRepository filmRepository;

    private final NamedParameterJdbcTemplate jdbc;

    private Map<String, Object> params;

    private MapSqlParameterSource sqlParameterSource;

    @BeforeEach
    void init() {
        sqlParameterSource = new MapSqlParameterSource();
        params = new HashMap<>();
    }

    @Test
    @DisplayName("должен возвращать фильм по идентификатору")
    public void should_return_film_when_find_by_id() {

        Optional<Film> filmOptional = filmRepository.findById(TEST_FILM_ID);

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm()
                );
    }

    @Test
    @DisplayName("Verify data.sql is loading data correctly")
    void testDataSqlLoading() {

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users", sqlParameterSource,  Integer.class);
        assertThat(count).isEqualTo(1);


        Integer filmCount = jdbc.queryForObject("SELECT COUNT(*) FROM films", sqlParameterSource,  Integer.class);
        assertThat(filmCount).isEqualTo(1);
    }

    @Test
    @DisplayName("сохранить фильм")
    void should_save_film() {
        Film film = getTestFilm();
        filmRepository.save(film);

        Optional<Film> savedFilm = filmRepository.findById(film.getId());

        assertThat(savedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("должен добавить лайк")
    void should_add_like_to_film() {

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        params.put("film_id", TEST_FILM_ID);
        params.put("user_id", TEST_USER_ID);
        sqlParameterSource.addValues(params);

        Integer likeCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = :film_id AND user_id = :user_id", sqlParameterSource,
                Integer.class
        );

        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("должен удалить лайк")
    void should_remove_like_from_film() {

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        filmRepository.removeLike(TEST_FILM_ID, TEST_USER_ID);

        params.put("film_id", TEST_FILM_ID);
        params.put("user_id", TEST_USER_ID);
        sqlParameterSource.addValues(params);

        sqlParameterSource.addValues(params);

        Integer likeCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = :film_id AND user_id = :user_id", sqlParameterSource,
                Integer.class
        );

        assertThat(likeCount).isEqualTo(0);

    }

    @Test
    @DisplayName("обновить фильм")
    void should_update_film() {
        Film film = getTestFilm();
        film.setName("updated name");
        filmRepository.update(film);

        Optional<Film> updatedFilm = filmRepository.findById(TEST_FILM_ID);
        assertThat(updatedFilm)
                .isPresent()
                .get()
                .extracting(Film::getName)
                .isEqualTo("updated name");
    }

    @Test
    @DisplayName("должен возвращать все фильмы")
    void should_return_all_films_when_values_called() {

        Film film1 = getTestFilm();
        Film film2 = Film.builder()
                .id(2L)
                .name("another name")
                .description("another desc")
                .releaseDate(Instant.now())
                .duration(90)
                .genres(getGenres())
                .mpa(getMpa())
                .build();

        filmRepository.save(film1);
        filmRepository.save(film2);

        Collection<Film> allFilms = filmRepository.values();

        assertThat(allFilms)
                .hasSize(3)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(1L, film1.getId(), film2.getId());
    }

    @Test
    @DisplayName("удалить фильм")
    void should_remove_film() {
        filmRepository.remove(getTestFilm());

        Optional<Film> removedFilm = filmRepository.findById(TEST_USER_ID);
        assertThat(removedFilm).isNotPresent();
    }

    @Test
    @DisplayName("получить популярные фильмы")
    void should_return_popular_films() {
        Film film1 = getTestFilm();
        Film film2 = Film.builder()
                .id(2L)
                .name("name")
                .description("description")
                .releaseDate(Instant.ofEpochMilli(1_114_608_000_000L))
                .duration(100)
                .genres(getGenres())
                .mpa(getMpa())
                .build();
        filmRepository.save(film1);
        filmRepository.save(film2);
        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        List<Film> popularFilms = filmRepository.getTopPopular();

        assertThat(popularFilms)
                .hasSize(3)
                .extracting(Film::getId)
                .containsExactly(TEST_FILM_ID, film1.getId(), film2.getId());
    }

    @Test
    @DisplayName("должен возвращать фильмы по жанру")
    void should_return_films_when_find_by_genre_called() {

        Film film1 = getTestFilm();
        Film film2 = Film.builder()
                .id(2L)
                .name("another name")
                .description("another desc")
                .releaseDate(Instant.now())
                .duration(90)
                .genres(new LinkedHashSet<>(Collections.singletonList(new Genre(2, "genre2"))))
                .mpa(getMpa())
                .build();

        filmRepository.save(film1);
        filmRepository.save(film2);

        Collection<Film> filmsByGenre = filmRepository.findFilmsByGenre(1L);

        assertThat(filmsByGenre)
                .hasSize(2)
                .extracting(Film::getId)
                .contains(TEST_FILM_ID);
    }

    private static LinkedHashSet<Genre> getGenres() {

        Genre genre = new Genre();
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);
        return genres;
    }

    private static Mpa getMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        return mpa;
    }

    private static Film getTestFilm() {

Film film = Film.builder()
        .id(TEST_FILM_ID)
        .name("name")
        .description("description")
        .releaseDate(Instant.ofEpochMilli(1_714_608_000_000L))
        .duration(100)
        .genres(getGenres())
        .mpa(getMpa())
        .build();

        return film;
    }
}