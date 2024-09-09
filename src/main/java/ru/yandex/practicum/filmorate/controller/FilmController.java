package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FilmDto add(@RequestBody NewFilmRequest filmRequest) {
        log.error("Film add {} start", filmRequest);
        FilmDto created = filmService.add(filmRequest);
        log.error("Added film is {}", created.getName());
        return created;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public FilmDto update(@RequestBody UpdateFilmRequest filmRequest) {
        log.error("Film update {} start", filmRequest);
        FilmDto updated = filmService.update(filmRequest);
        log.error("Updated film is {} complete", updated.getName());
        return updated;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.error("Add like film id {}, user id {} start", id, userId);
        filmService.addLike(id, userId);
        log.error("Added like film id {}, user id {} complete", id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.error("Remove like film id {}, user id {} start", id, userId);
        filmService.removeLike(id, userId);
        log.error("Removed like film id {}, user id {} complete", id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public FilmDto get(@PathVariable long id) {
        log.error("Get film with genre id {} start", id);
        FilmDto film = filmService.getById(id);
        log.error("Get film with genre id {} complete", id);
        return film;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(
            @RequestParam Optional<Integer> count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        log.info("Get popular films count {} with optional genre {} and year {} start",
                count, genreId, year);

        if (genreId != null || year != null) {
            return filmService.getPopularFilmsByGenresAndYears(count, genreId, year);
        }
            return filmService.getPopularFilms(count);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public boolean remove(@PathVariable Long id) {
        return filmService.remove(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilmsSort(
            @PathVariable("directorId") @Min(1) Long id,
            @RequestParam(required = false) List<String> sortBy) {
        log.info("Get director films sort, directorId: {}, sortBy: {}", id, sortBy);
        return filmService.getFilmsByDirector(id, sortBy);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Get common films for {} and {}.", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

}
