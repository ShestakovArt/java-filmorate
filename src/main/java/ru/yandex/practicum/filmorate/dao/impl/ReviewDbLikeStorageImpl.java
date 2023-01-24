package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDbLikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.enums.LikeStatus;

@Repository
@RequiredArgsConstructor
public class ReviewDbLikeStorageImpl implements ReviewDbLikeStorage {

    private final ReviewDbStorage reviewStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(final Integer reviewId, final Integer userId, LikeStatus likeStatus) {
        boolean status = likeStatus.getBoolean();
        if (checkLike(reviewId, userId, status) >= 0) {
            String sql = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, STATUS) " +
                    " VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, status);
            if (status) {
                reviewStorage.addUseful(reviewId);
            } else {
                reviewStorage.subUseful(reviewId);
            }
        }
    }

    @Override
    public void removeLike(final Integer reviewId, final Integer userId, LikeStatus likeStatus) {
        boolean status = likeStatus.getBoolean();
        if (checkLike(reviewId, userId, status) > 0) {
            String sql = "DELETE FROM REVIEW_LIKES " +
                    " WHERE REVIEW_ID = ? AND USER_ID = ? AND STATUS = ?;";
            jdbcTemplate.update(sql, reviewId, userId, status);
            if (status) {
                reviewStorage.subUseful(reviewId);
            } else {
                reviewStorage.addUseful(reviewId);
            }
        }
    }

    private int checkLike(final Integer reviewId, final long userId, boolean likeStatus) {
        String sql = "SELECT COUNT(*) FROM REVIEW_LIKES WHERE " +
                " REVIEW_ID = ? AND USER_ID = ? AND STATUS = ?;";

        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId, likeStatus);
    }
}
