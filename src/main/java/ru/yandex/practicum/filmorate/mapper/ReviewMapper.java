package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static Review mapToReview(NewReviewRequest reviewRequest) {
        Review review = new Review();
        review.setId(reviewRequest.getId());
        review.setReview(reviewRequest.getContent());
        review.setPositive(reviewRequest.isPositive());
        review.setUseful(reviewRequest.getUseful());
        review.setFilmId(reviewRequest.getFilmId());
        review.setUserId(reviewRequest.getUserId());
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setReview(review.getReview());
        dto.setPositive(review.isPositive());
        dto.setUseful(review.getUseful());
        dto.setFilmId(review.getFilmId());
        dto.setUserId(review.getUserId());
        return dto;
    }

    public static Review updateReviewFields(Review review, UpdateReviewRequest request) {
        /*if (request.hasName()) {
            review.setName(request.getName());
        }
        if (request.hasDescription()) {
            review.setDescription(request.getDescription());
        }
        if (request.hasDuration()) {
            review.setDuration(request.getDuration());
        }
        if (request.hasGenre()) {
            review.setGenres(request.getGenres());
        }
        if (request.hasMpa()) {
            review.setMpa(request.getMpa());
        }
        if (request.hasReleaseDate()) {
            review.setReleaseDate(request.getReleaseDate());
        }*/
        return review;
    }
}
