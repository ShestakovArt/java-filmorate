package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmDbStorage {

    int addFilm(Film film);

    void upgradeFilm(Film film);

    Optional<Film> findFilm(Integer id);

    Collection<Film> findAll();

    boolean deleteFilm(Integer filmId);

    List<Film> listMostPopularFilms(Integer limit, Integer genreId, Integer year);

    Collection<Film> findDirectorSortedFilms(Integer directorId, String[] sortBy);

    Collection<Film> findFilmsByDirector(String criteria);

    Collection<Film> findFilmsByTitle(String criteria);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
