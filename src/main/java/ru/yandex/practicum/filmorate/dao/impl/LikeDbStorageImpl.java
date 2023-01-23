package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class LikeDbStorageImpl implements LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Override
    public boolean addLikeFilm(Integer filmId, Integer userId) {
        String sqlQuery = "MERGE INTO USER_LIKE_FILM (FILM_ID, USER_ID) values ( ?, ? )";
        return jdbcTemplate.update(sqlQuery, filmId, userId) == 1;
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        if (findLikeUserToFilm(filmId, userId)) {
            String sqlQuery = "delete from USER_LIKE_FILM where FILM_ID = ? and USER_ID = ?";

            return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
        }
        return false;
    }

    @Override
    public boolean deleteAllLikes(Integer filmId) {
        String sqlQuery = String.format("delete\n" +
                "from USER_LIKE_FILM\n" +
                "where FILM_ID = %d", filmId);
        return jdbcTemplate.update(sqlQuery) > 0;
    }

    @Override
    public Map<Integer, Integer> getUsersCountOfLikedSameFilmsByUser(Integer userId) {
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


    private boolean findLikeUserToFilm(Integer filmId, Integer userId) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from USER_LIKE_FILM\n" +
                "where FILM_ID = %d and USER_ID = %d", filmId, userId);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

}
