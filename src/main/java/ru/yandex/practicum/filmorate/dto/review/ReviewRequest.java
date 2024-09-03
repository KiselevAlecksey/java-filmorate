package ru.yandex.practicum.filmorate.dto.review;

public interface ReviewRequest {
    Long getId();

    boolean isPositive();

    Integer getUseful();

    String getContent();

    Long getUserId();

    Long getFilmId();

    void setId(Long id);

    void setPositive(boolean isPositive);

    void setUseful(Integer useful);

    void setContent(String review);

    void setUserId(Long userId);

    void setFilmId(Long filmId);
}
