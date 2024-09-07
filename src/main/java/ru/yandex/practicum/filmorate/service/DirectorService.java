package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    DirectorDto getById(Long id);

    Collection<DirectorDto> getAll();

    DirectorDto create(NewDirectorRequest request);

    DirectorDto update(UpdateDirectorRequest request);

    boolean delete(Long id);
}
