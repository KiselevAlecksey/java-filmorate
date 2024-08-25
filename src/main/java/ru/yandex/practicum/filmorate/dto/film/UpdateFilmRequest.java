package ru.yandex.practicum.filmorate.dto.film;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Instant;
import java.util.LinkedHashSet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest implements FilmRequest {

    Long id;

    String name;

    String description;

    Integer duration;

    LinkedHashSet<Genre> genres;

    Mpa mpa;

    Instant releaseDate;

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

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null && releaseDate.isBefore(Instant.now());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
