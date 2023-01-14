package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        if (nameList.size() != 1) {
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

    @Override
    public List<Genre> getFilmGenres(Integer idFilm) {
        String sqlQuery = String.format("select GENRE_ID\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d", idFilm);
        List<Integer> idGenres = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        List<Genre> genreList = new ArrayList<>();

        for (Integer id : idGenres) {
            genreList.add(new Genre(id, findNameGenre(id)));
        }

        return genreList;
    }

    @Override
    public boolean setFilmGenre(Integer idFilm, Integer idGenre) {
        if (!findGenreToFilm(idFilm, idGenre)) {
            String sqlQuery = String.format("INSERT INTO FILM_TO_GENRE VALUES (%d, %d)", idFilm, idGenre);

            return jdbcTemplate.update(sqlQuery) == 1;
        }

        return true;
    }

    @Override
    public boolean deleteFilmGenre(Integer idFilm, Integer idGenre) {
        if (findGenreToFilm(idFilm, idGenre)) {
            String sqlQuery = "delete from FILM_TO_GENRE where FILM_ID = ? AND GENRE_ID = ?";

            return jdbcTemplate.update(sqlQuery, idFilm, idGenre) > 0;
        }

        return false;
    }


    private boolean findGenreToFilm(Integer idFilm, Integer idGenre) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d and GENRE_ID = %d", idFilm, idGenre);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }


}
