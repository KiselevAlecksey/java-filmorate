package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmService {
    Optional<FilmDto> get(Film film);

    FilmDto add(NewFilmRequest filmRequest);

    FilmDto update(UpdateFilmRequest filmRequest);

    FilmDto get(Long id);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<FilmDto> getPopularFilms(Optional<Integer> countOpt);

    Collection<FilmDto> findAll();

    List<Film> getFilmsByDirector(Long dirId, List<String> sortBy);

    boolean remove(Long id);

    Collection<FilmDto> getPopularFilmsByGenresAndYears(Optional<Integer> count, Integer genreId, Integer year);
}
