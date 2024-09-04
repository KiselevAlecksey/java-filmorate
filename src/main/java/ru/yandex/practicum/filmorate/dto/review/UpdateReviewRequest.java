package ru.yandex.practicum.filmorate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "reviewId")
public class UpdateReviewRequest implements ReviewRequest {

    Long reviewId;

    String content;

    @JsonProperty("isPositive")
    Boolean isPositive;

    @NotNull
    Long userId;

    @NotNull
    Long filmId;

    Integer useful;

    public boolean hasReviewId() {
        return reviewId != null;
    }

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }

    public boolean hasUserId() {
        return userId != null && userId > 0;
    }

    public boolean hasFilmId() {
        return filmId != null && filmId > 0;
    }

    public boolean hasUseful() {
        return useful != null;
    }
}
