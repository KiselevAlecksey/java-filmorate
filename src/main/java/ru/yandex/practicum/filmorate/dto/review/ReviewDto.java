package ru.yandex.practicum.filmorate.dto.review;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@JsonPropertyOrder({"id", "content", "isPositive", "userId", "filmId", "useful"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {

    Long id;

    String content;

    boolean isPositive;

    @NotNull
    Long userId;

    @NotNull
    Long filmId;

    Integer useful;
}
