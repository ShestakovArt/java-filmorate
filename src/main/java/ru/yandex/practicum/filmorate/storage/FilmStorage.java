package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Component
public interface FilmStorage {
    HashMap<Integer, Film> getFilms();
    Film addFilm(Film film);
    void deleteFilm(Integer id);
    void upgradeFilm(Film film);
}
