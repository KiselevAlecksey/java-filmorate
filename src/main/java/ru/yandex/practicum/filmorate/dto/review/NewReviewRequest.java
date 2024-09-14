package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "reviewId")
public class NewReviewRequest implements ReviewRequest {

    Long reviewId;

    @NotNull
    String content;

    @JsonProperty("isPositive")
    Boolean isPositive;

    @NotNull
    Long userId;

    @NotNull
    Long filmId;

    Integer useful;

}
