package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {

    List<Review> findAll();

    List<Review> getReviewTopForFilmId(Integer filmId, Long count);

    Review create(Review review);

    Review update(Review review);

    void remove(Integer reviewId);

    Review getReviewById(Integer reviewId);

    void addUseful(Integer reviewId);

    void subUseful(Integer reviewId);

    boolean isReviewExist(Integer reviewId);
}