package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.LikeStatus;

public interface ReviewDbLikeStorage {
    void addLike(Integer reviewId, Integer userId, LikeStatus status);

    void removeLike(Integer reviewId, Integer userId, LikeStatus status);

}