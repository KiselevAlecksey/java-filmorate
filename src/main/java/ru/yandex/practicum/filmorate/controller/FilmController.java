package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film add(@RequestBody Film film) {
        logger.error("Film add {} start", film);
        Film created = filmService.add(film);
        logger.error("Added film is {}", filmService.get(created));
        return created;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Film update(@RequestBody Film film) {
        logger.error("Film update {} start", film);
        Film updated = filmService.update(film);
        logger.error("Updated film is {} complete", filmService.get(updated));
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
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam int count) {
        logger.error("Get popular films count {} start", count);
        Collection<Film> popularFilms = filmService.getPopularFilms(count);
        logger.error("Get popular films count {} complete", count);
        return popularFilms;
    }
}
