package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryFilmRepository implements FilmRepository {

    private final Map<Long, Film> films;

    private final Map<Long, Set<Long>> likes;

    private final List<Film> popularFilms;

    @Override
    public void addLike(Long filmId, Long userId) {
        likes.computeIfAbsent(filmId, likes -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        likes.computeIfAbsent(filmId, likes -> new HashSet<>()).remove(userId);
    }

    @Override
    public void save(Film film) {
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        popularFilms.add(film);
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }

    @Override
    public Film remove(Film film) {
        Film removedFilm = films.remove(film.getId());
        popularFilms.remove(film);
        return removedFilm;
    }

    @Override
    public List<Film> getPopularFilms() {
        return popularFilms.stream()
                .sorted(Comparator.comparingLong((Film film) -> getLikesCount(film.getId())).reversed()
                        .thenComparing(Film::getId))
                .collect(Collectors.toList());
    }

    public Long getLikesCount(Long filmId) {
        return Optional.ofNullable(likes.get(filmId))
                .map(Set::size)
                .map(Long::valueOf)
                .orElse(0L);
    }
}
