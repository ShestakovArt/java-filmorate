package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

    private static final String findFilmsByDirectorsNameMatchesCriteria = "" +
            "SELECT f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_RELEASE_DATE, " +
            "f.FILM_DURATION, f.FILM_RATE, f.MPA_ID FROM FILMS f " +
            "RIGHT JOIN DIRECTOR_TO_FILM dtf ON dtf.FILM_ID = f.FILM_ID " +
            "RIGHT JOIN DIRECTOR dir ON dir.DIRECTOR_ID = dtf.DIRECTOR_ID " +
            "WHERE LOWER(dir.DIRECTOR_NAME) LIKE LOWER(?)";


    private static final String findFilmsByTitleMatchesCriteria = "" +
            "SELECT f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, " +
            "f.FILM_RELEASE_DATE, f.FILM_DURATION, f.FILM_RATE, f.MPA_ID " +
            "FROM FILMS f WHERE LOWER(f.FILM_NAME) LIKE LOWER(?)";

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
                , getRateAndLikeFilm(film.getId())
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
    public List<Film> listMostPopularFilms(Integer limit, Integer genreId, Integer year) {
        StringBuilder sqlQueryBuilder = new StringBuilder();
        sqlQueryBuilder
                .append("SELECT f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_RELEASE_DATE, ")
                .append("f.FILM_DURATION, f.FILM_RATE, f.MPA_ID, COUNT(ul.USER_ID) AS LIKES_COUNT FROM FILMS f ")
                .append("LEFT JOIN USER_LIKE_FILM ul ON ul.FILM_ID = f.FILM_ID ");
        if (genreId != null && year != null) {
            sqlQueryBuilder
                    .append("RIGHT JOIN FILM_TO_GENRE fg ON fg.FILM_ID = f.FILM_ID ")
                    .append("WHERE fg.GENRE_ID = ")
                    .append(genreId)
                    .append(" AND EXTRACT(YEAR FROM f.FILM_RELEASE_DATE) = ")
                    .append(year)
                    .append(' ');
        } else if (genreId != null) {
            sqlQueryBuilder
                    .append("RIGHT JOIN FILM_TO_GENRE fg ON fg.FILM_ID = f.FILM_ID ")
                    .append("WHERE fg.GENRE_ID = ")
                    .append(genreId)
                    .append(' ');
        } else if (year != null) {
            sqlQueryBuilder
                    .append("WHERE EXTRACT(YEAR FROM f.FILM_RELEASE_DATE) = ")
                    .append(year)
                    .append(' ');
        }
        sqlQueryBuilder
                .append("GROUP BY f.FILM_ID ORDER BY LIKES_COUNT DESC LIMIT ")
                .append(limit);

        return new LinkedList<>(jdbcTemplate.query(sqlQueryBuilder.toString(), this::mapRowToFilm));
    }

    @Override
    public boolean deleteFilm(Integer filmId) {
        String sqlQuery = String.format("delete\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d", filmId);
        jdbcTemplate.update(sqlQuery);

        // Удалить лайки

        sqlQuery = String.format("delete\n" +
                "from FILMS\n" +
                "where FILM_ID = %d", filmId);

        return jdbcTemplate.update(sqlQuery) > 0;
    }


    @Override
    public Collection<Film> findFilmsByDirector(String criteria) {
        return jdbcTemplate.query(
                findFilmsByDirectorsNameMatchesCriteria,
                this::mapRowToFilm,
                String.format("%%%s%%", criteria)
        );
    }

    @Override
    public Collection<Film> findFilmsByTitle(String criteria) {
        return jdbcTemplate.query(
                findFilmsByTitleMatchesCriteria,
                this::mapRowToFilm,
                String.format("%%%s%%", criteria)
        );
    }

    private Integer getRateAndLikeFilm(Integer filmId) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from USER_LIKE_FILM\n" +
                "where FILM_ID = %d", filmId);
        List<Integer> countRateAndLike = jdbcTemplate.queryForList(sqlQuery, Integer.class);

        return countRateAndLike.get(0);
    }


    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        Integer filmId = resultSet.getInt("FILM_ID");
        film.setId(filmId);
        film.setName(resultSet.getString("FILM_NAME"));
        film.setDescription(resultSet.getString("FILM_DESCRIPTION"));
        film.setReleaseDate(resultSet.getString("FILM_RELEASE_DATE"));
        film.setDuration(resultSet.getInt("FILM_DURATION"));
        Integer mpaId = resultSet.getInt("MPA_ID");
        film.setMpa(new Mpa(mpaId, mpaDbStorage.findNameMpa(mpaId)));
        film.setGenres(genreDbStorage.getFilmGenres(filmId));
        film.setRate(getRateAndLikeFilm(filmId));
        film.setDirectors(directorDbStorage.getFilmDirectors(filmId));

        return film;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = "select * " +
                "from FILMS " +
                "where FILM_ID in (" +
                "select first_user_likes.FILM_ID " +
                "from (" +
                "select FILM_ID " +
                "from USER_LIKE_FILM " +
                "where USER_ID = ?) as first_user_likes " +
                "join (" +
                "select FILM_ID " +
                "from USER_LIKE_FILM " +
                "where USER_ID = ?) as second_user_likes " +
                "on first_user_likes.FILM_ID = second_user_likes.FILM_ID) " +
                "order by FILM_RATE desc";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
    }
}