package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.LikeStatus;

public interface ReviewDbLikeStorage {
    void addLike(long reviewId, Integer userId, LikeStatus status);

    void removeLike(long reviewId, Integer userId, LikeStatus status);

}