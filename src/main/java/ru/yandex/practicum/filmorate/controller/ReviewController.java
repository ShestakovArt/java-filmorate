package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.enums.LikeStatus;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private final String pathId = "/{id}";
    private final String pathLike = pathId + "/like/{userId}";
    private final String pathDislike = pathId + "/dislike/{userId}";

    @GetMapping
    public List<Review> getReviewList(
            @RequestParam(defaultValue = "0", required = false) Integer filmId
            , @RequestParam(defaultValue = "10", required = false) Long count) {
        return reviewService.findAll(filmId, count);
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping(pathId)
    public void remove(@PathVariable("id") Integer reviewId) {
        reviewService.remove(reviewId);
    }

    @GetMapping(pathId)
    public Review getReview(@PathVariable("id") Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping(pathLike)
    public void addLike(
            @PathVariable("id") Integer reviewId,
            @PathVariable("userId") Integer userId) {
        reviewService.addLike(reviewId, userId, LikeStatus.LIKE);
    }

    @PutMapping(pathDislike)
    public void addDislike(
            @PathVariable("id") Integer reviewId,
            @PathVariable("userId") Integer userId) {
        reviewService.addLike(reviewId, userId, LikeStatus.DISLIKE);
    }

    @DeleteMapping(pathLike)
    public void removeLike(
            @PathVariable("id") Integer reviewId,
            @PathVariable("userId") Integer userId) {
        reviewService.removeLike(reviewId, userId, LikeStatus.LIKE);
    }

    @DeleteMapping(pathDislike)
    public void removeDislike(
            @PathVariable("id") Integer reviewId,
            @PathVariable("userId") Integer userId) {
        reviewService.removeLike(reviewId, userId, LikeStatus.DISLIKE);
    }
}