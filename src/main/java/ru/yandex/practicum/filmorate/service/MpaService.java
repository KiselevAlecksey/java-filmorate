package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.util.Collection;

public interface MpaService {

    MpaDto getById(Integer id);

    Collection<MpaDto> getAll();
}
