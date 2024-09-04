package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto create(@RequestBody NewReviewRequest reviewRequest) {
        log.error("Review create {} start", reviewRequest);
        ReviewDto created = reviewService.create(reviewRequest);
        log.error("Created review is {} complete", created.getId());
        return created;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto update(@RequestBody UpdateReviewRequest reviewRequest) {
        log.error("Review update {} start", reviewRequest);
        ReviewDto updated = reviewService.update(reviewRequest);
        log.error("Updated review is {} complete", updated.getId());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.error("Review delete id {} start", id);
        reviewService.remove(id);
        log.error("Deleted review id is {} complete", id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto getById(@PathVariable long id) {
        log.error("Review delete id {} start", id);
        ReviewDto reviewDto = reviewService.getById(id);
        log.error("Deleted review id is {} complete", id);
        return reviewDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ReviewDto> getAll(@RequestParam(required = false) long filmId, @RequestParam(required = false) long count) {
        log.error("Get all review by filmId {}, count {} start", filmId, count);
        Collection<ReviewDto> reviewDto = reviewService.getByReviewsId(filmId, count);
        log.error("Get all review by filmId {}, count {} complete", filmId, count);
        return reviewDto;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.error("Add like review id {}, user id {} start", id, userId);
        reviewService.addLike(id, userId);
        log.error("Added like film id {}, user id {} complete", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.error("Remove like review id {}, user id {} start", id, userId);
        reviewService.removeLike(id, userId);
        log.error("Removed like film id {}, user id {} complete", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.error("Add dislike review id {}, user id {} start", id, userId);
        reviewService.addDislike(id, userId);
        log.error("Added dislike film id {}, user id {} complete", id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        log.error("Remove dislike review id {}, user id {} start", id, userId);
        reviewService.removeDislike(id, userId);
        log.error("Removed dislike film id {}, user id {} complete", id, userId);
    }
}
