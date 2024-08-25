package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.NewGenreRequest;
import ru.yandex.practicum.filmorate.dto.UpdateGenreRequest;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {

    public static Genre mapToGenre(NewGenreRequest request) {
        Genre genre = new Genre();
        genre.setId(request.getId());
        genre.setName(request.getName());
        return genre;
    }

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre updateGenreFields(Genre genre, UpdateGenreRequest request) {
        if (request.hasName()) {
            genre.setName(request.getName());
        }
        return genre;
    }
}
