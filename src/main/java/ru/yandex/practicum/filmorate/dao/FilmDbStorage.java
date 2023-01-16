package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Component
public interface FilmDbStorage {
    int addFilm(Film film);

    void upgradeFilm(Film film);

    Optional<Film> findFilm(Integer id);

    Collection<Film> findAll();

    boolean addLikeFilm(Integer idFilm, Integer idUser);

    List<Film> listMostPopularFilms(int limit);

    boolean deleteLike(Integer idFilm, Integer idUser);
    boolean deleteFilm(Integer idFilm);
    List<Genre> getGenresFilm(Integer filmId);
    Collection<Film> findDirectorSortedFilms(Integer directorId, String[] sortBy);
}
