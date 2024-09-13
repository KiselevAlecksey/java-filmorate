package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class ModelConverter {
    public static LinkedHashSet<Genre> mapToGenre(LinkedHashSet<GenreDto> genres) {
        if (genres == null) {
            return null;
        }
        return genres.stream()
                .map(genreDto -> new Genre(genreDto.getId(), genreDto.getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static LinkedHashSet<Director> mapToDirector(LinkedHashSet<DirectorDto> directors) {
        if (directors == null) {
            return null;
        }
        return directors.stream()
                .map(directorDto -> new Director(directorDto.getId(), directorDto.getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Mpa mapToMpa(MpaDto mpaDto) {
        if (mpaDto == null) {
            return null;
        }
        return new Mpa(mpaDto.getId(), mpaDto.getName());
    }

    public static LinkedHashSet<GenreDto> mapToGenreDto(LinkedHashSet<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return genres.stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static LinkedHashSet<DirectorDto> mapToDirectorDto(LinkedHashSet<Director> directors) {
        if (directors == null) {
            return null;
        }
        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static MpaDto mapToMpaDto(Mpa mpa) {
        if (mpa == null) {
            return null;
        }
        return new MpaDto(mpa.getId(), mpa.getName());
    }
}
