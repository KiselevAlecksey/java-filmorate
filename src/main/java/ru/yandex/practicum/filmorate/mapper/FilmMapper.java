package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import static ru.yandex.practicum.filmorate.utils.ModelConverter.*;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {

        return Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .genres(mapToGenre(request.getGenres()))
                .mpa(mapToMpa(request.getMpa()))
                .directors(mapToDirector(request.getDirectors()))
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setDuration(film.getDuration());
        dto.setGenres(mapToGenreDto(film.getGenres()));
        dto.setMpa(mapToMpaDto(film.getMpa()));
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDirectors(mapToDirectorDto(film.getDirectors()));

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasGenre()) {
            film.setGenres(mapToGenre(request.getGenres()));
        }
        if (request.hasMpa()) {
            film.setMpa(mapToMpa(request.getMpa()));
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDirector()) {
            film.setDirectors(mapToDirector(request.getDirectors()));
        }
        return film;
    }
}
