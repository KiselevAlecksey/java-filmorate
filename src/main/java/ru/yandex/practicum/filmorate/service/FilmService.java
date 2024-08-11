package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film get(Film film);

    Film add(Film film);

    Film update(Film film);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Integer count);

    Collection<Film> findAll();
}
