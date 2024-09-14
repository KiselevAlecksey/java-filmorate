package ru.yandex.practicum.filmorate.dto.review;

public interface ReviewRequest {

    Long getReviewId();

    Boolean getIsPositive();

    Integer getUseful();

    String getContent();

    Long getUserId();

    Long getFilmId();

    void setUserId(Long userId);

    void setFilmId(Long filmId);
}
