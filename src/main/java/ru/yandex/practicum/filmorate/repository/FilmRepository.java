package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmRepository {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    void save(Film film);

    Film findById(Long id);

    Collection<Film> values();

    Film remove(Film film);

    List<Film> getPopularFilms();
}
