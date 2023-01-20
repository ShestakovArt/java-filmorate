package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Component
public interface FilmDbStorage {
    int addFilm(Film film);

    void upgradeFilm(Film film);

    Optional<Film> findFilm(Integer id);

    Collection<Film> findAll();

    boolean deleteFilm(Integer filmId);

    boolean addLikeFilm(Integer filmId, Integer userId);

    List<Film> listMostPopularFilms(int limit);

    boolean deleteLike(Integer filmId, Integer userId);

    Collection<Film> findDirectorSortedFilms(Integer directorId, String[] sortBy);

    Collection<Film> findFilmsByDirector(String criteria);

    Collection<Film> findFilmsByTitle(String criteria);

}
