package ru.yandex.practicum.filmorate.dal.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    Review updateReview(Review review);

    void remove(Long id);

    Optional<Review> getById(Long id);

    Collection<Review> getReviewsByFilmId(Long filmId, Integer count);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    void addDislike(Long id, Long userId);

    void removeDislike(Long id, Long userId);

}
