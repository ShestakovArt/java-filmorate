package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.RecommendationDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RecommendationDbStorageImpl implements RecommendationDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public RecommendationDbStorageImpl(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Map<Integer, Integer> getUsersCountOfLikedSameFilmsByUser(int userId) {
        String sqlQuery = "SELECT USER_ID, COUNT(USER_ID) COUNT_USER \n" +
                "FROM USER_LIKE_FILM \n" +
                "WHERE FILM_ID IN (SELECT FILM_ID \n" +
                "FROM USER_LIKE_FILM \n" +
                "WHERE USER_ID = ?) \n" +
                "AND USER_ID <> ? \n" +
                "GROUP BY USER_ID";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, userId, userId);
        Map<Integer, Integer> tempMapUserId = new HashMap<>();
        while (rs.next()) {
            tempMapUserId.put(rs.getInt("USER_ID"), rs.getInt("COUNT_USER"));
        }
        return tempMapUserId;
    }

    @Override
    public List<Film> getUserLikedFilms(Integer userId) {
        List<Film> tempListFilm = new ArrayList<>();
        String tempSqlQuery = "SELECT FILM_ID FROM USER_LIKE_FILM WHERE USER_ID = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(tempSqlQuery, userId);
        while (rs.next()) {
            Film tempFilm = filmDbStorage.findFilm(rs.getInt("FILM_ID")).get();
            tempListFilm.add(tempFilm);
        }
        return tempListFilm;
    }
}
