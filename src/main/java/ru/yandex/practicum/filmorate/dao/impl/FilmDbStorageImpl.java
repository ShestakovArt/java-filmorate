package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class FilmDbStorageImpl implements FilmDbStorage {
    private final JdbcTemplate jdbcTemplate;
    final MpaDbStorage mpaDbStorage;
    final GenreDbStorage genreDbStorage;
    final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate,
                             MpaDbStorage mpaDbStorage,
                             GenreDbStorage genreDbStorage,
                             DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public int addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
    }

    @Override
    public void upgradeFilm(Film film) {
        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, MPA_ID = ?, FILM_DESCRIPTION = ? , FILM_RELEASE_DATE = ?, FILM_DURATION = ?, FILM_RATE = ?" +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getMpa().getId()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getId());
    }

    @Override
    public Optional<Film> findFilm(Integer id) {
        String sqlQuery = "select FILM_ID, FILM_NAME, MPA_ID, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_RATE, FILM_RATE_AND_LIKES " +
                "from FILMS where FILM_ID = ?";

        return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "select FILM_ID, FILM_NAME, MPA_ID, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_RATE, FILM_RATE_AND_LIKES " +
                "from FILMS";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Collection<Film> findDirectorSortedFilms(Integer directorId, String[] sortBy) {
        StringBuilder sqlQuery = new StringBuilder("SELECT f.* FROM FILMS f ");
        if (sortBy[0].equals("likes")) {
            sqlQuery.append("left join (SELECT count(1) cnt, FILM_ID FROM USER_LIKE_FILM ulf GROUP BY FILM_ID) likes ON likes.FILM_ID=f.FILM_ID ");
        }
        sqlQuery.append("left join DIRECTOR_TO_FILM dtf on f.FILM_ID = dtf.FILM_ID ");
        sqlQuery.append("left join DIRECTOR d on dtf.DIRECTOR_ID = d.DIRECTOR_ID ");
        sqlQuery.append("WHERE d.DIRECTOR_ID=? ");

        if (sortBy[0].equals("year")) {
            sqlQuery.append("ORDER BY f.FILM_RELEASE_DATE");
        } else {
            sqlQuery.append("ORDER BY likes.cnt");
        }

        return jdbcTemplate.query(sqlQuery.toString(), this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> listMostPopularFilms(int limit) {
        Collection<Film> listAllFilms = findAll();
        for (Film film : listAllFilms) {
            String sqlQueryFindLike = String.format("" +
                    "SELECT COUNT(*)\n" +
                    "FROM USER_LIKE_FILM\n" +
                    "WHERE FILM_ID = %d", film.getId());
            List<Integer> countLikeToFilm = jdbcTemplate.queryForList(sqlQueryFindLike, Integer.class);
            film.setRateAndLikes(film.getRate() + countLikeToFilm.get(0));
            String sqlQueryUpdateFilm = "update FILMS set " +
                    "FILM_RATE_AND_LIKES = ? " +
                    "where FILM_ID = ?";

            jdbcTemplate.update(sqlQueryUpdateFilm
                    , film.getRateAndLikes()
                    , film.getId());
        }

        List<Film> mostPopularFilms = new ArrayList<>();
        String sqlQuery = String.format("SELECT FILM_ID\n" +
                "    FROM FILMS ORDER BY FILM_RATE_AND_LIKES DESC LIMIT %d", limit);
        List<Integer> listfilmIds = jdbcTemplate.queryForList(sqlQuery, Integer.class);

        if (listfilmIds.size() < 1) {
            throw new IncorrectParameterException("Список популярных фильмов пуст");
        }

        for (Integer id : listfilmIds) {
            mostPopularFilms.add(findFilm(id)
                    .orElseThrow(() -> new FilmNotFoundException("Фильм с идентификатором " + id + " не найден.")));
        }

        return mostPopularFilms;
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
    public boolean deleteFilm(Integer filmId) {




        String sqlQuery = String.format("delete\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d", filmId);
        System.out.println("sqlQuery = " + sqlQuery);
        jdbcTemplate.update(sqlQuery);

        sqlQuery = String.format("delete\n" +
                "from USER_LIKE_FILM\n" +
                "where FILM_ID = %d", filmId);
        jdbcTemplate.update(sqlQuery);

        sqlQuery = String.format("delete\n" +
                "from FILMS\n" +
                "where FILM_ID = %d", filmId);
        return jdbcTemplate.update(sqlQuery) > 0;
    }

    @Override
    public boolean addLikeFilm(Integer filmId, Integer userId) {
        if (!findLikeUserToFilm(filmId, userId)) {
            String sqlQuery = String.format("INSERT INTO USER_LIKE_FILM VALUES (%d, %d)", filmId, userId);

            return jdbcTemplate.update(sqlQuery) == 1;
        }

        return false;
    }

    private boolean findLikeUserToFilm(Integer filmId, Integer userId) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from USER_LIKE_FILM\n" +
                "where FILM_ID = %d and USER_ID = %d", filmId, userId);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private Integer getRateAndLikeFilm(Integer filmId) {
        String sqlQuery = String.format("select FILM_RATE_AND_LIKES\n" +
                "from FILMS\n" +
                "where FILM_ID = %d", filmId);
        List<Integer> countRateAndLike = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        if (countRateAndLike.size() > 0) {

            return countRateAndLike.get(0);
        } else {

            return 0;
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        Integer filmId = resultSet.getInt("FILM_ID");
        film.setId(filmId);
        film.setName(resultSet.getString("FILM_NAME"));
        film.setDescription(resultSet.getString("FILM_DESCRIPTION"));
        film.setReleaseDate(resultSet.getString("FILM_RELEASE_DATE"));
        film.setDuration(resultSet.getInt("FILM_DURATION"));
        film.setRate(resultSet.getInt("FILM_RATE"));
        Integer mpaId = resultSet.getInt("MPA_ID");
        film.setMpa(new Mpa(mpaId, mpaDbStorage.findNameMpa(mpaId)));
        film.setGenres(genreDbStorage.getFilmGenres(filmId));
        film.setRateAndLikes(getRateAndLikeFilm(filmId));
        film.setDirectors(directorDbStorage.getFilmDirectors(filmId));
        return film;
    }
}
