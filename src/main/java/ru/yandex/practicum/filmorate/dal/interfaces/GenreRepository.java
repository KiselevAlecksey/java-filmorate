package ru.yandex.practicum.filmorate.dal.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    void save(Genre genre);

    void update(Genre genre);

    Optional<Genre> findById(Integer id);

    Collection<Genre> values();

    List<Integer> getAllGenreIds();

    Collection<Genre> getFilmGenres(Long filmId);

    List<Genre> getByIds(List<Integer> ids);
}
