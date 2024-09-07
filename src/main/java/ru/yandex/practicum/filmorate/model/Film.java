package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * Film.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Film {

    Long id;

    @NotNull
    String name;

    String description;

    @NotNull
    Integer duration;

    LinkedHashSet<Genre> genres;

    Mpa mpa;

    @Builder.Default
    Instant releaseDate = Instant.ofEpochMilli(0L);

    LinkedHashSet<Director> directors;
}
