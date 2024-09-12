package ru.yandex.practicum.filmorate.dto.film;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Instant;
import java.util.LinkedHashSet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFilmRequest implements FilmRequest {

    String name;

    String description;

    Integer duration;

    LinkedHashSet<Genre> genres;

    Mpa mpa;

    Instant releaseDate;

    LinkedHashSet<Director> directors;
}
