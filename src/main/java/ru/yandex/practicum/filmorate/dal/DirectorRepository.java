package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    List<Integer> getAllIds();

    Optional<Director> getById(Long id);

    Director create(Director director);

    Director update(Director director);

    Collection<Director> values();

    boolean delete(Long id);

    List<Director> getByIds(List<Long> ids);
}
