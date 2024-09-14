package ru.yandex.practicum.filmorate.dto.film;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;

import java.time.Instant;
import java.util.LinkedHashSet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFilmRequest implements FilmRequest {

    String name;

    String description;

    Integer duration;

    LinkedHashSet<GenreDto> genres;

    MpaDto mpa;

    Instant releaseDate;

    LinkedHashSet<DirectorDto> directors;
}
