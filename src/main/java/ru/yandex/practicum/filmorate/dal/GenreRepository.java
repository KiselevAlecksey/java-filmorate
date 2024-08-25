package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Genre save(Genre film);

    Genre update(Genre film);

    Optional<Genre> findById(Integer id);

    Collection<Genre> values();

    List<Integer> getAllGenreIds();

    Collection<Genre> getFilmGenres(Long filmId);
}
