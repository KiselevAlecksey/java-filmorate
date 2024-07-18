package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")

public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    static final int MAX_LENGTH_DESCRIPTION = 200;
    static final long CINEMA_BURN_DAY = -2335564800000L;
    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film add(@RequestBody Film film ) {
        logger.debug("Film {} check validation", film);

        if (film.getName().isBlank() || film.getDescription().length() > MAX_LENGTH_DESCRIPTION
                || film.getReleaseDate().isBefore(Instant.ofEpochMilli(CINEMA_BURN_DAY))
                || film.getDuration() < 0) {

            logger.trace("Название {} не может быть пустым," +
                    " описание {} не может быть больше " + MAX_LENGTH_DESCRIPTION +
                    " дата релиза {} не может быть раньше " + CINEMA_BURN_DAY +
                    " продолжительность {} не может быть отрицательной",
                    film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

            throw new ConditionsNotMetException("Название не может быть пустым," +
                    " описание не может быть больше " + MAX_LENGTH_DESCRIPTION +
                    " дата релиза не может быть раньше " + CINEMA_BURN_DAY +
                    " продолжительность не может быть отрицательной");
        }

        logger.trace("Created a new film");
        Film putFilm = Film.builder()
                .id(getNextId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
        logger.debug("Created film is {}", putFilm);
        films.put(putFilm.getId(), putFilm);
        logger.trace("Added film");
        return putFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        logger.debug("Film {} check validation", film);
        if (film.getId() == null) {
            logger.trace("Id = {} should be pointed", (Object) null);
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (films.containsKey(film.getId())) {

            if (film.getName().isBlank() || film.getDescription().length() > MAX_LENGTH_DESCRIPTION
                    || film.getReleaseDate().isBefore(Instant.ofEpochMilli(CINEMA_BURN_DAY))
                    || film.getDuration() < 0) {

                logger.trace("Название {} не может быть пустым," +
                                " описание {} не может быть больше " + MAX_LENGTH_DESCRIPTION +
                                " дата релиза {} не может быть раньше " + CINEMA_BURN_DAY +
                                " продолжительность {} не может быть отрицательной",
                        film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

                throw new ConditionsNotMetException("Название не может быть пустым," +
                        " описание не может быть больше " + MAX_LENGTH_DESCRIPTION +
                        " дата релиза не может быть раньше " + CINEMA_BURN_DAY +
                        " продолжительность не может быть отрицательной");
            }
            logger.trace("Updated film");

            Film updateFilm = films.get(film.getId()).builder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .build();
            logger.debug("Updated film is {} ", updateFilm);
            films.put(film.getId(), film);
            logger.trace("Updated film");
            return updateFilm;
        }
        logger.trace("Film with id = {} not found", film.getId());
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private long getNextId() {
        logger.trace("Created new id");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        logger.trace("New id is {} ", currentMaxId);
        return ++currentMaxId;
    }
}
