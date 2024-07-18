package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Film.
 */
@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    Long id;
    @NotNull
    String name;
    String description;
    @Builder.Default
    Instant releaseDate = Instant.ofEpochMilli(0L);
    @NotNull
    Integer duration;
}
