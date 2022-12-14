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
    boolean setGenreFilm(Integer idFilm, Integer idGenre);
    boolean deleteGenreFilm(Integer idFilm, Integer idGenre);
    boolean addLikeFilm(Integer idFilm, Integer idUser);
    List<Film> listMostPopularFilms(int limit);
    boolean deleteLike(Integer idFilm, Integer idUser);
    List<Genre> getGenresFilm(Integer filmId);
}
