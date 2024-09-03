package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.interfaces.ReviewService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DefaultReviewService implements ReviewService {

    @Autowired
    @Qualifier("JdbcReviewRepository")
    private final ReviewRepository reviewRepository;

    @Autowired
    @Qualifier("JdbcUserRepository")
    private final UserRepository userRepository;

    private final Validator validator;

    @Override
    public ReviewDto create(NewReviewRequest reviewRequest) {

        validator.validateReviewRequest(reviewRequest);

        Review putReview = ReviewMapper.mapToReview(reviewRequest);

        putReview = reviewRepository.save(putReview);

        return ReviewMapper.mapToReviewDto(putReview);
    }

    @Override
    public ReviewDto update(UpdateReviewRequest reviewRequest) {

        validator.validateReviewRequest(reviewRequest);

        Review updatedReview = reviewRepository.getById(reviewRequest.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        ReviewMapper.updateReviewFields(updatedReview, reviewRequest);

        reviewRepository.updateReview(updatedReview);

        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    @Override
    public void remove(Long id) {

        reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        reviewRepository.remove(id);
    }

    @Override
    public ReviewDto getById(long id) {

        Review review = reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        return ReviewMapper.mapToReviewDto(review);
    }

    @Override
    public Collection<ReviewDto> getByReviewsId(Long filmId, Long count) {

        return reviewRepository.getReviewsByFilmId(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    @Override
    public void addLike(long id, long userId) {

        Review review = reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        reviewRepository.addLike(id, userId);
    }

    @Override
    public void removeLike(long id, long userId) {
        Review review = reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        reviewRepository.removeLike(id, userId);
    }

    @Override
    public void addDislike(long id, long userId) {
        Review review = reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        reviewRepository.addDislike(id, userId);
    }

    @Override
    public void removeDislike(long id, long userId) {
        Review review = reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        reviewRepository.removeDislike(id, userId);
    }
}
