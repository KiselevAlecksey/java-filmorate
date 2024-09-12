package ru.yandex.practicum.filmorate.dto.film;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Constant;

import java.time.Instant;
import java.util.LinkedHashSet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest implements FilmRequest {

    Long id;

    String name;

    String description;

    Integer duration;

    LinkedHashSet<GenreDto> genres;

    MpaDto mpa;

    Instant releaseDate;

    LinkedHashSet<DirectorDto> directors;

    public boolean hasName() {
        return isNotBlank(name);
    }

    public boolean hasDescription() {
        return isNotBlank(description);
    }

    public boolean hasDuration() {
        return duration != null && duration > 0;
    }

    public boolean hasGenre() {
        return genres != null && !genres.isEmpty();
    }

    public boolean hasDirector() {
        return directors != null && !directors.isEmpty();
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null && releaseDate.isAfter(Instant.ofEpochMilli(Constant.CINEMA_BURN_DAY));
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
