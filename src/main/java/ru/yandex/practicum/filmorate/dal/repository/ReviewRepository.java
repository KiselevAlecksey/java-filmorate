package ru.yandex.practicum.filmorate.dal.repository;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    Review update(Review review);

    void remove(Long id);

    Optional<Review> getById(Long id);

    Collection<Review> getReviewsByFilmId(Long filmId, Long count);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    void addDislike(Long id, Long userId);

    void removeDislike(Long id, Long userId);

    Collection<Review> getAllReviews(Long count);

    Collection<Review> getAllReviews();
}
