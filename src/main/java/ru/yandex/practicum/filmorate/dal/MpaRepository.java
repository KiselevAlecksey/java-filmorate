package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MpaRepository {
    Mpa save(Mpa mpa);

    Mpa update(Mpa mpa);

    Optional<Mpa> findById(Integer id);

    Collection<Mpa> values();

    List<Integer> getAllMpaIds();
}
