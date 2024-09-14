package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dto.genre.GenreDto;

import java.util.Collection;

public interface GenreService {

    GenreDto getById(Integer id);

    Collection<GenreDto> getAll();
}
