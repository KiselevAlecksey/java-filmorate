package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.Collection;

public interface GenreService {

    GenreDto getById(Integer id);

    Collection<GenreDto> getAll();
}