package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmService {

    Optional<FilmDto> getById(Film film);

    FilmDto add(NewFilmRequest filmRequest);

    FilmDto update(UpdateFilmRequest filmRequest);

    FilmDto getById(Long id);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<FilmDto> getPopularFilms(Optional<Integer> countOpt);

    Collection<FilmDto> findAll();

    List<Film> getFilmsByDirector(Long dirId, List<String> sortBy);

    boolean remove(Long id);

    Collection<FilmDto> getPopularFilmsByGenresAndYears(Optional<Integer> count, Integer genreId, Integer year);

    Collection<FilmDto> getCommonFilms(Long userId, Long friendId);

    Set<Film> search(String query, String[] searchFields);
}
