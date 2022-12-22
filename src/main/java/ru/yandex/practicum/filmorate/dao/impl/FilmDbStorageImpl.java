package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class FilmDbStorageImpl implements FilmDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
    }

    @Override
    public boolean deleteFilm(Integer id) {
        return false;
    }

    @Override
    public void upgradeFilm(Film film) {

    }

    @Override
    public Optional<Film> findFilm(Integer id) {
        return Optional.empty();
    }

    @Override
    public Collection<Film> findAll() {
        return null;
    }
}
