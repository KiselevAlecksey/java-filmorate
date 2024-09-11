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
        review.setReviewId(reviewRequest.getReviewId());
        review.setContent(reviewRequest.getContent());
        review.setIsPositive(reviewRequest.getIsPositive());
        review.setUserId(reviewRequest.getUserId());
        review.setFilmId(reviewRequest.getFilmId());
        review.setUseful(reviewRequest.getUseful());
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());

        return dto;
    }

    public static Review updateReviewFields(Review review, UpdateReviewRequest request) {
        if (request.hasReviewId()) {
            review.setReviewId(request.getReviewId());
        }
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }
        if (request.hasIsPositive()) {
            review.setIsPositive(request.getIsPositive());
        }
        return review;
    }
}
