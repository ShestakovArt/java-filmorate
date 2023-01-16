package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {

    List<Review> findAll();

    List<Review> getReviewTopForFilmId(Integer filmId, Long count);

    Review create(Review review);

    Review update(Review review);

    void remove(Long reviewId);

    Review getReviewById(Long reviewId);

    void addUseful(long reviewId);

    void subUseful(long reviewId);

    boolean isReviewExist(long reviewId);
}