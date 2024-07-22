package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FilmRepository implements BaseRepository<Film> {

    private Map<Long, Film> films;

    public FilmRepository() {
        this.films = new HashMap<>();
    }

    @Override
    public void save(Film film) {
        films.put(film.getId(), film);
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
    public Film get(Long id) {
        return films.get(id);
    }
}
