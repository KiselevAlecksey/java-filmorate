package ru.yandex.practicum.filmorate.dto.film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Instant;
import java.util.LinkedHashSet;

public interface FilmRequest {

    String getName();

    String getDescription();

    Instant getReleaseDate();

    Integer getDuration();

    Mpa getMpa();

    LinkedHashSet<Genre> getGenres();

    void setGenres(LinkedHashSet<Genre> genres);

    void setMpa(Mpa mpa);
}
