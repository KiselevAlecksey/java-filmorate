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
import ru.yandex.practicum.filmorate.dal.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.dal.interfaces.UserRepository;
import ru.yandex.practicum.filmorate.dal.mapper.*;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.utils.TestDataFactory.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.TestDataFactory;

@JdbcTest
@Import({JdbcFilmRepository.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class,
        JdbcUserRepository.class, UserRowMapper.class, DirectorRowMapper.class,
        JdbcDirectorRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository")
class JdbcFilmRepositoryTest {

    private final DirectorRepository directorRepository;

    private final FilmRepository filmRepository;

    private final UserRepository userRepository;

    private final NamedParameterJdbcTemplate jdbc;

    private Map<String, Object> params;

    private MapSqlParameterSource sqlParameterSource;

    @BeforeEach
    void init() {
        sqlParameterSource = new MapSqlParameterSource();
        params = new HashMap<>();

        jdbc.update("DELETE FROM films", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1", new MapSqlParameterSource());

        jdbc.update("DELETE FROM directors", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE directors ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

    }

    @Test
    @DisplayName("Verify data.sql is loading data correctly")
    void testDataSqlLoading() {

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users", sqlParameterSource, Integer.class);
        assertThat(count).isEqualTo(COUNT_ONE);

        filmRepository.save(TEST_FILM);
        Integer filmCount = jdbc.queryForObject("SELECT COUNT(*) FROM films", sqlParameterSource, Integer.class);
        assertThat(filmCount).isEqualTo(COUNT_ONE);
    }

    @Test
    @DisplayName("должен обновить поле жанры")
    void should_update_genres() {
        directorRepository.create(TEST_DIRECTOR);

        Film film = filmRepository.save(TEST_FILM);
        film.setName("updated name");
        film.setGenres(new LinkedHashSet<>());
        Film filmUpdated = filmRepository.update(film);

        Optional<Film> updatedFilm = filmRepository.getByIdFullDetails(TEST_FILM_ID);

        assertThat(updatedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("должен обновлять поле режиссеры фильма")
    public void should_update_field_directors_film() {

        Film film = filmRepository.save(TEST_FILM);
        LinkedHashSet<Director> set = new LinkedHashSet<>();

        filmRepository.update(film);
        Optional<Film> filmOptional = filmRepository.getByIdFullDetails(TEST_FILM_ID);
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("должен возвращать фильм по идентификатору")
    public void should_return_film_when_find_by_id() {

        Film film = filmRepository.save(TEST_FILM);

        Optional<Film> filmOptional = filmRepository.getByIdFullDetails(TEST_FILM_ID);

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("сохранить фильм")
    void should_save_film() {

        Film film = filmRepository.save(TEST_FILM);

        Optional<Film> savedFilm = filmRepository.getByIdFullDetails(TEST_FILM_ID);

        assertThat(savedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("должен добавить лайк")
    void should_add_like_to_film() {

        Film film = filmRepository.save(TEST_FILM);

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
        Film film1 = filmRepository.save(TEST_FILM);
        Film film2 = filmRepository.save(TestDataFactory.getTestFilm(film1));

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

        Film film = filmRepository.save(TEST_FILM);
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
        Film film1 = filmRepository.save(TEST_FILM);
        Film film2 = filmRepository.save(TestDataFactory.getTestFilm(film1));

        Collection<Film> allFilms = filmRepository.values();

        assertThat(allFilms)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(film1, film2));
    }

    @Test
    @DisplayName("должен возвращать фильмы по названию или режиссёру")
    void should_return_films_when_director_called() {

        Film film1 = filmRepository.save(TEST_FILM);
        film1.setName("Film Updated");
        Film film2 = filmRepository.save(TestDataFactory.getTestFilm(film1));

        String query = "upDatE";
        String[] searchFields = {"title", "director"};

        List<Film> filmsSort = filmRepository.search(query, searchFields);

        assertThat(filmsSort)
                .usingRecursiveComparison()
                .isEqualTo(Collections.singletonList(film2));
    }

    @Test
    @DisplayName("удалить фильм")
    void should_remove_film() {
        filmRepository.remove(TEST_FILM.getId());

        Optional<Film> removedFilm = filmRepository.getByIdPartialDetails(TEST_USER_ID);

        assertThat(removedFilm).isNotPresent();
    }

    @Test
    @DisplayName("получить популярные фильмы")
    void should_return_popular_films() {
        Film film1 = filmRepository.save(TEST_FILM);
        Film film2 = filmRepository.save(TestDataFactory.getTestFilm(film1));

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);

        List<Film> popularFilms = filmRepository.getTopPopular();

        assertThat(popularFilms)
                .hasSize(TEST_INT_TWO)
                .extracting(Film::getId)
                .containsExactly(TEST_FILM_ID, TEST_FILM2_ID);
        assertThat(popularFilms.getFirst()).isEqualTo(film1);
    }

    @Test
    @DisplayName("должен возвращать популярные фильмы по жанру и году")
    void should_return_popular_films_by_genre_and_year() {
        userRepository.save(TEST_USER);
        Film film1 = filmRepository.save(TEST_FILM);
        Film film2 = filmRepository.save(TestDataFactory.getTestFilm(film1));

        film2.setReleaseDate(TEST_RELEASE_DATE2);

        Film film3 = filmRepository.save(TestDataFactory.getTestFilm(film2));

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        filmRepository.addLike(TEST_FILM2_ID, TEST_USER_ID);
        filmRepository.addLike(TEST_FILM3_ID, TEST_USER_ID);

        List<Film> popularFilms = filmRepository.getPopularFilmsByGenreAndYear(
                TEST_COUNT_TWO,
                TEST_INT_ONE,
                TEST_INT_YEAR);

        assertThat(popularFilms)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(TEST_FILM_ID, TEST_FILM2_ID);
    }

    @Test
    @DisplayName("должен возвращать фильмы по жанру")
    void should_return_films_when_find_by_genre_called() {

        Film film1 = filmRepository.save(TEST_FILM);
        Film film2 = filmRepository.save(getTestFilm(film1));

        Collection<Film> films = filmRepository.values();

        assertThat(films)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(film1, film2));
    }

    @Test
    @DisplayName("должен возвращать рекомендованные фильмы")
    void should_return_recommended_films() {
        Film film1 = filmRepository.save(TEST_FILM);
        Film film2 = filmRepository.save(getTestFilm(film1));

        User user1 = userRepository.save(TEST_USER);
        User user2 = userRepository.save(TestDataFactory.getTestUser(user1));

        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        filmRepository.addLike(TEST_FILM_ID, TEST_USER2_ID);
        filmRepository.addLike(TEST_FILM2_ID, TEST_USER_ID);
        Director director = TEST_DIRECTOR;

        List<Film> recommendedFilms = filmRepository.getRecommendedFilms(TEST_USER2_ID);

        assertThat(recommendedFilms)
                .hasSize(TEST_INT_ONE)
                .first()
                .usingRecursiveComparison()
                .isEqualTo(film2);
    }
}