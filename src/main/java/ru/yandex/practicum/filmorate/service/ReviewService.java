package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;

import java.util.Collection;

public interface ReviewService {

    ReviewDto create(NewReviewRequest reviewRequest);

    ReviewDto update(UpdateReviewRequest reviewRequest);

    void remove(long id);

    ReviewDto getById(long id);

    Collection<ReviewDto> getByReviewsId(long id, long count);

    void addLike(long id, long userId);

    void removeLike(long id, long userId);

    void addDislike(long id, long userId);

    void removeDislike(long id, long userId);
}