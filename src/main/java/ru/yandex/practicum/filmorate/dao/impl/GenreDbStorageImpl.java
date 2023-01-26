package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorageImpl implements GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre findGenreById(Integer id) {
        try {
            String sqlQuery = "select GENRE_ID, GENRE_NAME from GENRE where GENRE_ID = ?";
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id)).get();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
    public List<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = String.format("select GENRE_ID\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d", filmId);
        List<Integer> idGenres = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        List<Genre> genreList = new ArrayList<>();

        for (Integer id : idGenres) {
            genreList.add(findGenreById(id));
        }

        return genreList;
    }

    @Override
    public boolean setFilmGenre(Integer filmId, Integer idGenre) {
        Genre genreById = findGenreById(idGenre);
        if (genreById == null) {
            return false;
        } else if (!findGenreToFilm(filmId, idGenre)) {
            try {
                String sqlQuery = String.format("INSERT INTO FILM_TO_GENRE VALUES (%d, %d)", filmId, idGenre);
                return jdbcTemplate.update(sqlQuery) == 1;
            } catch (Exception e) {
                log.info(e.getMessage());
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean deleteFilmGenre(Integer filmId, Integer idGenre) {
        if (findGenreToFilm(filmId, idGenre)) {
            String sqlQuery = "delete from FILM_TO_GENRE where FILM_ID = ? AND GENRE_ID = ?";

            return jdbcTemplate.update(sqlQuery, filmId, idGenre) > 0;
        }

        return false;
    }

    private boolean findGenreToFilm(Integer filmId, Integer idGenre) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d and GENRE_ID = %d", filmId, idGenre);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }
}
