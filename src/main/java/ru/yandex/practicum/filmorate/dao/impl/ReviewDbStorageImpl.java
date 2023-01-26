package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorageImpl implements ReviewDbStorage {

    private final JdbcTemplate jdbcTemplate;

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("REVIEW_ID"));
        review.setContent(rs.getString("CONTENT"));
        review.setIsPositive(rs.getBoolean("POSITIVE"));
        review.setUseful(rs.getInt("USEFUL"));
        review.setUserId(rs.getInt("USER_ID"));
        review.setFilmId(rs.getInt("FILM_ID"));

        return review;
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC;";

        List<Review> reviews = jdbcTemplate.query(sql, this::makeReview);

        log.info("Получить лист отзывов из БД: {}.", reviews.size());

        return reviews;
    }

    @Override
    public List<Review> getReviewTopForFilmId(Integer filmId, Long count) {
        String sql = "SELECT * FROM REVIEWS " +
                " WHERE FILM_ID = ? " +
                " ORDER BY USEFUL DESC " +
                " LIMIT ?;";

        List<Review> reviews = jdbcTemplate.query(sql, this::makeReview, filmId, count);
        log.info("Получить лист отзывов из БД: {}.", reviews.size());

        return reviews;
    }

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO REVIEWS (CONTENT, POSITIVE, USEFUL, USER_ID, FILM_ID) " +
                " VALUES(? , ? , ? , ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"review_id"});
                    prSt.setString(1, review.getContent());
                    prSt.setBoolean(2, review.getIsPositive());
                    prSt.setLong(3, review.getUseful());
                    prSt.setLong(4, review.getUserId());
                    prSt.setLong(5, review.getFilmId());
                    return prSt;
                }
                , keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Создать отзыв: {}.", review.getReviewId());

        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE REVIEWS " +
                " SET CONTENT = ?, POSITIVE = ? " +
                " WHERE REVIEW_ID = ? ;";

        jdbcTemplate.update(sql
                , review.getContent()
                , review.getIsPositive()
                , review.getReviewId()
        );

        return getReviewById(review.getReviewId());
    }

    @Override
    public void remove(Integer reviewId) {
        String sql = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? ;";
        jdbcTemplate.update(sql, reviewId);
        sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = ? ;";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?;";
        List<Review> reviews = jdbcTemplate.query(sql, this::makeReview, reviewId);

        return reviews.get(0);
    }

    @Override
    public void addUseful(Integer reviewId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?; ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void subUseful(Integer reviewId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?; ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public boolean isReviewExist(Integer reviewId) {
        String sql = "SELECT COUNT(*) FROM REVIEWS WHERE REVIEW_ID = ? ;";
        int reviewCount = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);

        return reviewCount > 0;
    }
}