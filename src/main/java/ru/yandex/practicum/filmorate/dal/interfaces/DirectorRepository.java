package ru.yandex.practicum.filmorate.dal.interfaces;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    List<Long> getAllIds();

    Optional<Director> getById(Long id);

    Director create(Director director);

    Director update(Director director);

    Collection<Director> values();

    boolean delete(Long id);

    List<DirectorDto> getByIds(List<Long> ids);
}
