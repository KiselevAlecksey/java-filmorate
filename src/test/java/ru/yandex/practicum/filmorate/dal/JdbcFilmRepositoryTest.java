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
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@JdbcTest
@Import({JdbcFilmRepository.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class,
        JdbcUserRepository.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository")
class JdbcFilmRepositoryTest {
    public static final long TEST_USER_ID = 1L;
    public static final long TEST_USER2_ID = 2L;
    public static final long TEST_FILM_ID = 1L;
    public static final long TEST_FILM2_ID = 2L;
    public static final long TEST_FILM3_ID = 3L;
    public static final int TEST_LIKE_COUNT = 1;
    public static final int COUNT_ZERO = 0;
    public static final int COUNT_ONE = 1;

    private final JdbcFilmRepository filmRepository;

    private final JdbcUserRepository userRepository;

    private final NamedParameterJdbcTemplate jdbc;

    private Map<String, Object> params;

    private MapSqlParameterSource sqlParameterSource;

    @BeforeEach
    void init() {
        sqlParameterSource = new MapSqlParameterSource();
        params = new HashMap<>();

        jdbc.update("DELETE FROM films", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());
    }

    @Test
    @DisplayName("Verify data.sql is loading data correctly")
    void testDataSqlLoading() {

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users", sqlParameterSource,  Integer.class);
        assertThat(count).isEqualTo(COUNT_ONE);

        filmRepository.save(getTestFilm());
        Integer filmCount = jdbc.queryForObject("SELECT COUNT(*) FROM films", sqlParameterSource,  Integer.class);
        assertThat(filmCount).isEqualTo(COUNT_ONE);

    }

    @Test
    @DisplayName("должен возвращать фильм по идентификатору")
    public void should_return_film_when_find_by_id() {
        filmRepository.save(getTestFilm());
        Optional<Film> filmOptional = filmRepository.getByIdFullDetails(TEST_FILM_ID);

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("сохранить фильм")
    void should_save_film() {

        filmRepository.save(getTestFilm());
        Optional<Film> savedFilm = filmRepository.getByIdFullDetails(TEST_FILM_ID);

        assertThat(savedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("должен добавить лайк")
    void should_add_like_to_film() {

        filmRepository.save(getTestFilm());

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        params.put("film_id", TEST_FILM_ID);
        params.put("user_id", TEST_USER_ID);
        sqlParameterSource.addValues(params);

        Integer likeCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = :film_id AND user_id = :user_id", sqlParameterSource,
                Integer.class
        );

        assertThat(likeCount).isEqualTo(TEST_LIKE_COUNT);
    }

    @Test
    @DisplayName("должен удалить лайк")
    void should_remove_like_from_film() {
        Film film1 = getTestFilm();
        Film film2 = getTestFilm2(film1);

        filmRepository.save(film1);
        filmRepository.save(film2);

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

        assertThat(likeCount).isEqualTo(COUNT_ZERO);

    }

    @Test
    @DisplayName("обновить фильм")
    void should_update_film() {

        Film film = filmRepository.save(getTestFilm());
        film.setName("updated name");
        filmRepository.update(film);

        Optional<Film> updatedFilm = filmRepository.getByIdFullDetails(TEST_FILM_ID);
        assertThat(updatedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("должен возвращать все фильмы")
    void should_return_all_films_when_values_called() {
        Film film1 = getTestFilm();
        Film film2 = getTestFilm2(film1);

        filmRepository.save(film1);
        filmRepository.save(film2);

        Collection<Film> allFilms = filmRepository.values();

        assertThat(allFilms)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(film1, film2));
    }

    @Test
    @DisplayName("удалить фильм")
    void should_remove_film() {
        filmRepository.remove(getTestFilm());

        Optional<Film> removedFilm = filmRepository.getByIdPartialDetails(TEST_USER_ID);
        assertThat(removedFilm).isNotPresent();
    }

    @Test
    @DisplayName("получить популярные фильмы")
    void should_return_popular_films() {
        Film film1 = getTestFilm();
        Film film2 = getTestFilm2(film1);

        filmRepository.save(film1);
        filmRepository.save(film2);
        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        List<Film> popularFilms = filmRepository.getTopPopular();

        assertThat(popularFilms)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(TEST_FILM_ID, TEST_FILM2_ID);
        assertThat(popularFilms.getFirst()).isEqualTo(film1);
    }

    @Test
    @DisplayName("должен возвращать популярные фильмы по жанру и году")
    void should_return_popular_films_by_genre_and_year() {
        Film film1 = getTestFilm();
        Film film2 = getTestFilm2(film1);
        Film film3 = getTestFilm3(film2);

        filmRepository.save(film1);
        filmRepository.save(film2);
        filmRepository.save(film3);

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        filmRepository.addLike(TEST_FILM2_ID, TEST_USER_ID);
        filmRepository.addLike(TEST_FILM3_ID, TEST_USER_ID);

        List<Film> popularFilms = filmRepository.getPopularFilmsByGenreAndYear(
                Optional.of(2),
                1,
                2024);

        assertThat(popularFilms)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(TEST_FILM_ID, TEST_FILM2_ID);
    }

    @Test
    @DisplayName("должен возвращать фильмы по жанру")
    void should_return_films_when_find_by_genre_called() {

        Film film1 = getTestFilm();
        Film film2 = getTestFilm2(film1);

        filmRepository.save(film1);
        filmRepository.save(film2);

        Collection<Film> films = filmRepository.values();

        assertThat(films)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(film1, film2));
    }

  /*  @Test
    @DisplayName("должен возвращать рекомендованные фильмы")
    void should_return_recommended_films() {
        Film film1 = getTestFilm();
        Film film2 = getTestFilm2(film1);

        filmRepository.save(film1);
        filmRepository.save(film2);

        User user1 = getTestUser();
        userRepository.save(user1);
        User user2 = getTestUser2();
        userRepository.save(user2);

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        filmRepository.addLike(TEST_FILM_ID, TEST_USER2_ID);
        filmRepository.addLike(TEST_FILM2_ID, TEST_USER2_ID);

        List<Film> recommendedFilms = filmRepository.getRecommendedFilms(TEST_USER_ID);

        assertThat(recommendedFilms)
                .hasSize(1)
                .first()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }*/

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
        return Film.builder()
                .id(TEST_FILM_ID)
                .name("name")
                .description("description")
                .releaseDate(Instant.ofEpochMilli(1_714_608_000_000L))
                .duration(100)
                .genres(getGenres())
                .mpa(getMpa())
                .build();
    }

    private static Film getTestFilm2(Film film) {
        return film.toBuilder()
                .id(TEST_FILM2_ID)
                .name("another name")
                .description("another desc")
                .releaseDate(Instant.ofEpochMilli(1_714_608_000_000L))
                .duration(90)
                .genres(getGenres())
                .mpa(getMpa())
                .build();
    }

    private static User getTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("example@email.ru")
                .name("name")
                .login("description")
                .birthday(Instant.ofEpochMilli(1_714_608_000_000L))
                .build();
    }

    private static User getTestUser2() {
        return User.builder()
                .id(TEST_USER2_ID)
                .email("example@email.ru")
                .name("name")
                .login("description")
                .birthday(Instant.ofEpochMilli(1_714_608_000_000L))
                .build();
    }

    private static Film getTestFilm3(Film film) {
        return film.toBuilder()
                .id(TEST_FILM3_ID)
                .name("third name")
                .description("third desc")
                .releaseDate(Instant.ofEpochMilli(1_515_468_800_000L)) // 2018-03-01
                .duration(110)
                .genres(getGenres())
                .mpa(getMpa())
                .build();
    }
}