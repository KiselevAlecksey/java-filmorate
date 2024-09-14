package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.interfaces.ReviewService;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto create(@RequestBody NewReviewRequest reviewRequest) {
        log.error("Review create {} start", reviewRequest);
        ReviewDto created = reviewService.create(reviewRequest);
        log.error("Created review is {} complete", created.getReviewId());
        return created;
    }

    @PutMapping
    public ReviewDto update(@RequestBody UpdateReviewRequest reviewRequest) {
        log.error("Review update {} start", reviewRequest);
        ReviewDto updated = reviewService.update(reviewRequest);
        log.error("Updated review is {} complete", updated.getReviewId());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.error("Review delete id {} start", id);
        reviewService.remove(id);
        log.error("Deleted review id is {} complete", id);
    }

    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable long id) {
        log.error("Review delete id {} start", id);
        ReviewDto reviewDto = reviewService.getById(id);
        log.error("Deleted review id is {} complete", id);
        return reviewDto;
    }

    @GetMapping
    public Collection<ReviewDto> getAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false) @Positive Integer count) {

        log.error("Get all review by filmId {}, count {} start", filmId, count);
        Collection<ReviewDto> reviewDto = reviewService.getByReviewsId(filmId, count);
        log.error("Get all review by filmId {}, count {} complete", filmId, count);
        return reviewDto;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.error("Add like review id {}, user id {} start", id, userId);
        reviewService.addLike(id, userId);
        log.error("Added like review id {}, user id {} complete", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.error("Remove like review id {}, user id {} start", id, userId);
        reviewService.removeLike(id, userId);
        log.error("Removed like review id {}, user id {} complete", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.error("Add dislike review id {}, user id {} start", id, userId);
        reviewService.addDislike(id, userId);
        log.error("Added dislike review id {}, user id {} complete", id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        log.error("Remove dislike review id {}, user id {} start", id, userId);
        reviewService.removeDislike(id, userId);
        log.error("Removed dislike review id {}, user id {} complete", id, userId);
    }
}
