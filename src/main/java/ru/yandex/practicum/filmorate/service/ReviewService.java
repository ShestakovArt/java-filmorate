package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbLikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.enums.LikeStatus;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

import static ru.yandex.practicum.filmorate.enums.EventOperation.*;
import static ru.yandex.practicum.filmorate.enums.EventType.REVIEW;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDbStorage reviewStorage;
    private final ReviewDbLikeStorage reviewLikeStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    public List<Review> findAll(Integer filmId, Long count) {
        if (filmId > 0) {
            if (filmStorage.findFilm(filmId).isPresent()) {
                return reviewStorage.getReviewTopForFilmId(filmId, count);
            } else {
                throw new FilmNotFoundException("Идентификатор фильма не найден.");
            }
        } else {
            return reviewStorage.findAll();
        }
    }

    public Review create(Review review) {
        if (!userStorage.findUser(review.getUserId()).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        if (!filmStorage.findFilm(review.getFilmId()).isPresent()) {
            throw new FilmNotFoundException("Фильм не найден.");
        }
        Review reviewAssignId = reviewStorage.create(review);
        userStorage.recordEvent(reviewAssignId.getUserId(), reviewAssignId.getReviewId(), REVIEW, ADD);

        return reviewAssignId;
    }

    public Review update(Review review) {
        if (reviewStorage.isReviewExist(review.getReviewId())) {
            Review reviewAssignId = reviewStorage.update(review);
            userStorage.recordEvent(reviewAssignId.getUserId(), reviewAssignId.getReviewId(), REVIEW, UPDATE);

            return reviewAssignId;
        } else {
            throw new ReviewNotFoundException("Ошибка обновления, отзыв не найден.");
        }
    }

    public void remove(Integer reviewId) {
        if (reviewStorage.isReviewExist(reviewId)) {
            Review review = reviewStorage.getReviewById(reviewId);
            reviewStorage.remove(reviewId);
            userStorage.recordEvent(review.getUserId(), review.getReviewId(), REVIEW, REMOVE);
        } else {
            throw new ReviewNotFoundException("Отзыв для удаления не найден.");
        }
    }

    public Review getReviewById(Integer reviewId) {
        if (reviewStorage.isReviewExist(reviewId)) {
            return reviewStorage.getReviewById(reviewId);
        } else {
            throw new ReviewNotFoundException("Отзыв с таким идентификатором не найден.");
        }
    }

    public void addLike(Integer reviewId, Integer userId, LikeStatus likeStatus) {
        checkNotFound(reviewId, userId);
        reviewLikeStorage.addLike(reviewId, userId, likeStatus);
    }

    public void removeLike(Integer reviewId, Integer userId, LikeStatus likeStatus) {
        checkNotFound(reviewId, userId);
        reviewLikeStorage.removeLike(reviewId, userId, likeStatus);
    }

    public void checkNotFound(Integer reviewId, Integer userId) {
        if (!reviewStorage.isReviewExist(reviewId)) {
            throw new ReviewNotFoundException("Отзыв с таким идентификатором не найден.");
        }
        if (!userStorage.findUser(userId).isPresent()) {
            throw new UserNotFoundException("Отзыв с таким идентификатором не найден.");
        }
    }
}
