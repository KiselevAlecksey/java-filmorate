package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {
    Optional<FilmDto> get(Film film);

    FilmDto add(NewFilmRequest filmRequest);

    FilmDto update(UpdateFilmRequest filmRequest);

    FilmDto get(Long id);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<FilmDto> getPopularFilms(Optional<Integer> count);

    Collection<FilmDto> findAll();

    Collection<FilmDto> getPopularFilmsByGenresAndYears(Optional<Integer> count, int genreId, int year);
}
