package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class GenreDbStorageImpl implements GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String findNameGenre(Integer id) {
        String sqlQuery = String.format("select GENRE_NAME " +
                "from GENRE where GENRE_ID = %d", id);
        List<String> nameList = jdbcTemplate.queryForList(sqlQuery, String.class);

        if(nameList.size() != 1){
            throw new ValidationException("Не коректный ID GENRE");
        }

        return nameList.get(0);
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "select GENRE_ID, GENRE_NAME from GENRE";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

        private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre(resultSet.getInt("GENRE_ID")
                , resultSet.getString("GENRE_NAME"));

        return genre;
    }
}
