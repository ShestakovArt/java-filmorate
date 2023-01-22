package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorageImpl implements DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void deleteDirector(int directorId) {
        deleteDirectorFromFilms(directorId);

        String sqlQuery = "delete from DIRECTOR where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, directorId);
    }

    private void deleteDirectorFromFilms(Integer directorId) {
        String sqlQuery = String.format("DELETE FROM DIRECTOR_TO_FILM WHERE DIRECTOR_ID=?");

        jdbcTemplate.update(sqlQuery, directorId);
    }

    @Override
    public void deleteFilmDirector(Integer filmId, Integer directorId) {
        String sqlQuery = String.format("DELETE FROM DIRECTOR_TO_FILM WHERE FILM_ID=? and DIRECTOR_ID=?");

        jdbcTemplate.update(sqlQuery, filmId, directorId);
    }

    @Override
    public int add(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTOR")
                .usingGeneratedKeyColumns("DIRECTOR_ID");

        return simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
    }

    @Override
    public Collection<Director> findAll() {
        String sqlQuery = "select DIRECTOR_ID,DIRECTOR_NAME from DIRECTOR";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Director> findById(Integer id) {
        try {
            String sqlQuery = "select DIRECTOR_ID,DIRECTOR_NAME from DIRECTOR where DIRECTOR_ID = ?";
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            log.info("not found director by id: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public void updateDirector(Director director) {
        Optional<Director> currentDirector = findById(director.getId());
        if (!currentDirector.isEmpty()) {
            String sqlQuery = "update DIRECTOR set DIRECTOR_NAME=? where DIRECTOR_ID = ?";
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        } else {
            throw new DirectorNotFoundException("Режисер с идентификатором " + director.getId() + " не найден.");
        }
    }

    private Director mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director(
                resultSet.getInt("DIRECTOR_ID")
                , resultSet.getString("DIRECTOR_NAME")
        );

        return director;
    }

    @Override
    public List<Director> getFilmDirectors(Integer filmId) {
        String sqlQuery = String.format("select DIRECTOR_ID " +
                "from DIRECTOR_TO_FILM\n" +
                "where FILM_ID = %d", filmId);

        List<Director> directors = new ArrayList<>();

        List<Integer> directorsIdList = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        for (Integer id : directorsIdList) {
            directors.add(findById(id).get());
        }

        return directors;
    }

    @Override
    public void addFilmDirector(Integer filmId, Integer directorId) {
        String sqlQuery = String.format("INSERT INTO DIRECTOR_TO_FILM (FILM_ID, DIRECTOR_ID) VALUES (%d, %d)", filmId, directorId);

        jdbcTemplate.update(sqlQuery);
    }
}
