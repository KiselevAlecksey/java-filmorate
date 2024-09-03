package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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

