package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Film.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {

    Long id;

    @NotNull
    String name;

    String description;

    @NotNull
    Integer duration;

    @Builder.Default
    Instant releaseDate = Instant.ofEpochMilli(0L);
}
