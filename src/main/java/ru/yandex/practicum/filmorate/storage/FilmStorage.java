package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {

    HashMap<Integer, Film> getFilms();

    Film addFilm(Film film);

    void deleteFilm(Integer id);

    void upgradeFilm(Film film);
}
