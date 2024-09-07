package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {

    public static Director mapToDirector(NewDirectorRequest request) {
        Director director = new Director();
        director.setId(request.getId());
        director.setName(request.getName());
        return director;
    }

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }

    public static Director updateDirectorFields(Director director, UpdateDirectorRequest request) {
        if (request.hasName()) {
            director.setName(request.getName());
        }
        return director;
    }
}
