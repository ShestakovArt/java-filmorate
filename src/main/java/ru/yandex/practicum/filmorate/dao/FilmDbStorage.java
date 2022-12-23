package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;


@Component
public interface FilmDbStorage {
    int addFilm(Film film);
    boolean deleteFilm(Integer id);
    void upgradeFilm(Film film);
    Optional<Film> findFilm(Integer id);
    Collection<Film> findAll();
    void setGenreFilm(Integer idFilm, Integer idGenre);
}
