package ru.yandex.practicum.filmorate.dto.review;

public interface ReviewRequest {

    Long getReviewId();

    Boolean getIsPositive();

    Integer getUseful();

    String getContent();

    Long getUserId();

    Long getFilmId();

    void setReviewId(Long id);

    void setIsPositive(Boolean isPositive);

    void setUseful(Integer useful);

    void setContent(String review);

    void setUserId(Long userId);

    void setFilmId(Long filmId);
}
