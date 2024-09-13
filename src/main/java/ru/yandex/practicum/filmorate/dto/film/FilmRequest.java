package ru.yandex.practicum.filmorate.dto.film;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;

import java.time.Instant;
import java.util.LinkedHashSet;

public interface FilmRequest {

    String getName();

    String getDescription();

    Instant getReleaseDate();

    Integer getDuration();

    MpaDto getMpa();

    LinkedHashSet<GenreDto> getGenres();

    void setGenres(LinkedHashSet<GenreDto> genres);

    void setMpa(MpaDto mpa);

    LinkedHashSet<DirectorDto> getDirectors();

    void setDirectors(LinkedHashSet<DirectorDto> directors);
}
