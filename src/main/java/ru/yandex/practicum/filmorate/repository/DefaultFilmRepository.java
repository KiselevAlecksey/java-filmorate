package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DefaultFilmRepository implements FilmRepository {

    private final Map<Long, Film> films;

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

}
