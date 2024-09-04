package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FilmDto add(@RequestBody NewFilmRequest filmRequest) {
        logger.error("Film add {} start", filmRequest);
        FilmDto created = filmService.add(filmRequest);
        logger.error("Added film is {}", created.getName());
        return created;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public FilmDto update(@RequestBody UpdateFilmRequest filmRequest) {
        logger.error("Film update {} start", filmRequest);
        FilmDto updated = filmService.update(filmRequest);
        logger.error("Updated film is {} complete", updated.getName());
        return updated;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        logger.error("Add like film id {}, user id {} start", id, userId);
        filmService.addLike(id, userId);
        logger.error("Added like film id {}, user id {} complete", id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        logger.error("Remove like film id {}, user id {} start", id, userId);
        filmService.removeLike(id, userId);
        logger.error("Removed like film id {}, user id {} complete", id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public FilmDto getFilmWithGenre(@PathVariable long id) {
        logger.error("Get film with genre id {} start", id);
        FilmDto film = filmService.get(id);
        logger.error("Get film with genre id {} complete", id);
        return film;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(
            @RequestParam Optional<Integer> count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        logger.info("Get popular films count {} with optional genre {} and year {} start",
                count, genreId, year);

        if (genreId != null && year != null) {
            return filmService.getPopularFilmsByGenresAndYears(count, genreId, year);
        } else {
            return filmService.getPopularFilms(count);
        }
    }

}
