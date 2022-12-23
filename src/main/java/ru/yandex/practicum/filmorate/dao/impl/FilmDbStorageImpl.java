package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbStorageImpl implements FilmDbStorage {
    private final JdbcTemplate jdbcTemplate;
    final MpaService mpaService;
    final GenreService genreService;

    @Autowired
    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService){
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
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
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public void upgradeFilm(Film film) {
        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, MPA_ID = ?, FILM_DESCRIPTION = ? , FILM_RELEASE_DATE = ?, FILM_DURATION = ?" +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getMpa().getId()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getId());
    }

    @Override
    public Optional<Film> findFilm(Integer id) {
        String sqlQuery = "select FILM_ID, FILM_NAME, MPA_ID, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION " +
                "from FILMS where FILM_ID = ?";
        return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "select FILM_ID, FILM_NAME, MPA_ID, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION " +
                "from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public boolean setGenreFilm(Integer idFilm, Integer idGenre){
        if(!findGenreToFilm(idFilm, idGenre)){
            String sqlQuery = String.format("INSERT INTO FILM_TO_GENRE VALUES (%d, %d)", idFilm, idGenre);
            return jdbcTemplate.update(sqlQuery) == 1;
        }
        return false;
    }

    @Override
    public boolean deleteGenreFilm(Integer idFilm, Integer idGenre){
        if(findGenreToFilm(idFilm, idGenre)){
            String sqlQuery = "delete from FILM_TO_GENRE where FILM_ID = ? AND GENRE_ID = ?";
            return jdbcTemplate.update(sqlQuery, idFilm, idGenre) > 0;
        }
        return false;
    }

    @Override
    public boolean addLikeFilm(Integer idFilm, Integer idUser){
        if(!findLikeUserToFilm(idFilm, idUser)){
            String sqlQuery = String.format("INSERT INTO USER_LIKE_FILM VALUES (%d, %d)", idFilm, idUser);
            return jdbcTemplate.update(sqlQuery) == 1;
        }
        return false;
    }

    @Override
    public List<Film> listMostPopularFilms(int limit){
        List<Film> mostPopularFilms = new ArrayList<>();
        String sqlQuery = String.format("SELECT FILM_ID\n" +
                "    FROM (SELECT FILM_ID, COUNT(*) FROM USER_LIKE_FILM GROUP BY FILM_ID LIMIT %d)", limit);
        List<Integer> listIdFilms = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        if(listIdFilms.size() < 1){
            throw new ValidationException("Список популярных фильмов пуст");
        }
        for(Integer id : listIdFilms){
            mostPopularFilms.add(findFilm(id)
                    .orElseThrow(() ->new FilmNotFoundException("Фильм с идентификатором " + id + " не найден.")));
        }
        return mostPopularFilms;
    }

    @Override
    public boolean deleteLike(Integer idFilm, Integer idUser) {
        if(!findLikeUserToFilm(idFilm, idUser)){
            String sqlQuery = "delete from USER_LIKE_FILM where FILM_ID = ? and USER_ID = ?";
            return jdbcTemplate.update(sqlQuery, idFilm, idUser) > 0;
        }
        return false;
    }

    @Override
    public List<Genre> getGenresFilm(Integer filmId){
        String sqlQuery = String.format("select GENRE_ID\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d", filmId);
        List<Integer> idGenres = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        List<Genre> genreList = new ArrayList<>();
        for (Integer id : idGenres){
            genreList.add(genreService.getGenre(id));
        }
        return genreList;
    }

    private boolean findGenreToFilm(Integer idFilm, Integer idGenre) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from FILM_TO_GENRE\n" +
                "where FILM_ID = %d and GENRE_ID = %d", idFilm, idGenre);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private boolean findLikeUserToFilm(Integer idFilm, Integer idUser) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from USER_LIKE_FILM\n" +
                "where FILM_ID = %d and USER_ID = %d", idFilm, idUser);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(resultSet.getString("FILM_NAME")
                , resultSet.getString("FILM_DESCRIPTION")
                , resultSet.getString("FILM_RELEASE_DATE")
                , resultSet.getInt("FILM_DURATION")
                , 0
                , mpaService.getMpa(resultSet.getInt("MPA_ID"))
        , new ArrayList<>());
        film.setId(resultSet.getInt("FILM_ID"));
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));
        film.setGenres(getGenresFilm(film.getId()));
        return film;
    }
}