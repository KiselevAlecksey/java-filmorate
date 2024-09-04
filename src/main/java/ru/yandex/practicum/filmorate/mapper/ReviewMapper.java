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
        review.setContent(reviewRequest.getContent());
        review.setPositive(reviewRequest.isPositive());
        review.setUserId(reviewRequest.getUserId());
        review.setFilmId(reviewRequest.getFilmId());
        review.setUseful(reviewRequest.getUseful());
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setPositive(review.isPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        System.out.println(dto);
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
