package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "reviewId")
public class Review {

    Long reviewId;

    @NotNull
    String content;

    Boolean isPositive;

    @NotNull
    Long userId;

    @NotNull
    Long filmId;

    Integer useful;

}

