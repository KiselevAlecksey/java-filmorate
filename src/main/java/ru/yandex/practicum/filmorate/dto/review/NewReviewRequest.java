package ru.yandex.practicum.filmorate.dto.review;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewReviewRequest implements ReviewRequest {

    Long id;

    boolean isPositive;

    Integer useful;

    String content;

    Long userId;

    Long filmId;
}
