package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;

public interface BaseRepository<T> {

    void save(T t);

    T findById(Long id);

    Collection<T> values();

    T get(Long id);
}
