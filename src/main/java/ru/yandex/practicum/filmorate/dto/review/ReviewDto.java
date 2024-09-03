package ru.yandex.practicum.filmorate.dto.review;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {

    Long id;

    boolean isPositive;

    Integer useful;

    String review;

    Long userId;

    Long filmId;
}
