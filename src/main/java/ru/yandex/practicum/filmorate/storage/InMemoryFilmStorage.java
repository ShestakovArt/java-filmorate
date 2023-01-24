package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private HashMap<Integer, Film> films = new HashMap<>();
    private int counterId = 1;

    @Override
    public HashMap<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(counterId);
        films.put(film.getId(), film);
        counterId++;
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        if (films.containsKey(id)) {
            films.remove(id);
        }
    }

    @Override
    public void upgradeFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
    }
}
