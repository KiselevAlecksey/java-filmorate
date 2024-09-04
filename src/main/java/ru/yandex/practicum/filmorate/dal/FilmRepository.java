package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Film save(Film film);

    Film update(Film film);

    Optional<Film> getByIdPartialDetails(Long id);

    Collection<Film> values();

    boolean remove(Long id);

    List<Film> getTopPopular();

    Collection<Film> findFilmsByGenre(Long id);

    Optional<Film> getByIdFullDetails(Long id);

    List<Film> getPopularFilmsByGenreAndYear(Optional<Integer> countOpt, Integer genreId, Integer year);


    List<Film> getRecommendedFilms(Long userId);

}
