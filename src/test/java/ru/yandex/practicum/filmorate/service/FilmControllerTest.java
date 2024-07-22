package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.resouces.FilmDescription;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.service.FilmController.CINEMA_BURN_DAY;
import static ru.yandex.practicum.filmorate.service.FilmController.MAX_LENGTH_DESCRIPTION;

@DisplayName("Контроллер пользователей")
public class FilmControllerTest {
    FilmController filmController;
    Film defaultFilm;
    Instant releaseDate = Instant.ofEpochMilli(-2335564800000L);
    Integer duration;
    Instant wrongReleaseDate = Instant.ofEpochMilli(-2335564800001L);

    @BeforeEach
    public void init() {
        duration = 15;
        defaultFilm = new Film(1L, "Название", "Описание", releaseDate, duration);
        filmController = new FilmController();
    }

    @DisplayName("Должен добавить новый фильм")
    @Test
    public void shouldAddNewFilm() {
        filmController.add(defaultFilm);
        assertEqualsFilm(defaultFilm, filmController.films.get(defaultFilm.getId()), "фильмы не совпадают");
    }

    @DisplayName("Должен обновить описание фильма")
    @Test
    public void shouldUpdateFilm() {
        Film expectedUpdateFilm = defaultFilm.toBuilder()
                .name("Новое название")
                .description("Новое описание")
                .releaseDate(Instant.ofEpochMilli(0L))
                .duration(0)
                .build();
        filmController.add(defaultFilm);
        filmController.update(expectedUpdateFilm);
        assertEqualsFilm(expectedUpdateFilm, filmController.films.get(defaultFilm.getId()), "фильмы не совпадают");
    }

    @DisplayName("Должен вернуть список всех фильмов")
    @Test
    public void shouldReturnListOfFilms() {
        Map<Long, Film> expectedFilms = new HashMap<>();
        expectedFilms.put(defaultFilm.getId(), defaultFilm);
        filmController.films.save(defaultFilm);
        assertEquals(expectedFilms.size(), filmController.findAll().size());
        assertEqualsFilm(expectedFilms.get(defaultFilm.getId()), filmController.films.get(defaultFilm.getId()), "фильмы не совпадают");
    }

    @DisplayName("Не должен добавлять новый фильм c пустым названием")
    @Test
    public void shouldNotAddNewFilmWithBlankName() {
        Film expectedUpdateFilm = defaultFilm.toBuilder()
                .name(" ")
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.add(expectedUpdateFilm);
        });
        String expectedMessage = "Название не может быть пустым," +
                " описание не может быть больше " + MAX_LENGTH_DESCRIPTION +
                " дата релиза не может быть раньше " + CINEMA_BURN_DAY +
                " продолжительность не может быть отрицательной";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("Не должен добавлять новый фильм c описанием больше установленного")
    @Test
    public void shouldNotAddNewFilmWithDescriptionOverMaxSet() {
        Film expectedUpdateFilm = defaultFilm.toBuilder()
                .description(FilmDescription.DESCRIPTION)
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.add(expectedUpdateFilm);
        });
        String expectedMessage = "Название не может быть пустым," +
                " описание не может быть больше " + MAX_LENGTH_DESCRIPTION +
                " дата релиза не может быть раньше " + CINEMA_BURN_DAY +
                " продолжительность не может быть отрицательной";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("Не должен добавлять новый фильм c датой релиза раньше установленной")
    @Test
    public void shouldNotAddNewFilmWithReleaseDateIsBeforeSet() {
        Film expectedUpdateFilm = defaultFilm.toBuilder()
                .releaseDate(wrongReleaseDate)
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.add(expectedUpdateFilm);
        });
        String expectedMessage = "Название не может быть пустым," +
                " описание не может быть больше " + MAX_LENGTH_DESCRIPTION +
                " дата релиза не может быть раньше " + CINEMA_BURN_DAY +
                " продолжительность не может быть отрицательной";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("Не должен добавлять новый фильм c отрицательной продолжительностью")
    @Test
    public void shouldNotAddNewFilmWithNegativeDuration() {
        Film expectedUpdateFilm = defaultFilm.toBuilder()
                .duration(-100)
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            filmController.add(expectedUpdateFilm);
        });
        String expectedMessage = "Название не может быть пустым," +
                " описание не может быть больше " + MAX_LENGTH_DESCRIPTION +
                " дата релиза не может быть раньше " + CINEMA_BURN_DAY +
                " продолжительность не может быть отрицательной";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    private void assertEqualsFilm(Film expected, Film actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getReleaseDate(), actual.getReleaseDate(), message + ", releaseDate");
        assertEquals(expected.getDuration(), actual.getDuration(), message + ", duration");
    }
}
