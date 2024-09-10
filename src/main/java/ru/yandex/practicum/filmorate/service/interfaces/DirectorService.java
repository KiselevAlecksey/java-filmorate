package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;

import java.util.Collection;

public interface DirectorService {

    DirectorDto getById(Long id);

    Collection<DirectorDto> getAll();

    DirectorDto create(NewDirectorRequest request);

    DirectorDto update(UpdateDirectorRequest request);

    boolean delete(Long id);
}
