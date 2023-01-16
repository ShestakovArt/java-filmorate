package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbLikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.LikeStatus;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

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
        return  reviewStorage.create(review);
    }

    public Review update(Review review) {
        if (reviewStorage.isReviewExist(review.getReviewId())) {
        return reviewStorage.update(review);
        } else {
            throw new ReviewNotFoundException("Ошибка обновления, отзыв не найден.");
        }
    }

    public void remove(Long reviewId) {
        if (reviewStorage.isReviewExist(reviewId)) {
        reviewStorage.remove(reviewId);
        } else {
            throw new ReviewNotFoundException("Отзыв для удаления не найден.");
        }
    }

    public Review getReviewById(Long reviewId) {
        if (reviewStorage.isReviewExist(reviewId)) {
        return reviewStorage.getReviewById(reviewId);
        } else {
            throw new ReviewNotFoundException("Отзыв с таким идентификатором не найден.");
        }
    }

    public void addLike(Long reviewId, Integer userId, LikeStatus likeStatus) {
        checkNotFound(reviewId, userId);
        reviewLikeStorage.addLike(reviewId, userId, likeStatus);
    }

    public void removeLike(Long reviewId, Integer userId, LikeStatus likeStatus) {
        checkNotFound(reviewId, userId);
        reviewLikeStorage.removeLike(reviewId, userId, likeStatus);
    }

    public void checkNotFound (Long reviewId, Integer userId) {
        if (!reviewStorage.isReviewExist(reviewId)) {
            throw new ReviewNotFoundException("Отзыв с таким идентификатором не найден.");
        }
        if (!userStorage.findUser(userId).isPresent()) {
            throw new UserNotFoundException("Отзыв с таким идентификатором не найден.");
        }
    }

}
