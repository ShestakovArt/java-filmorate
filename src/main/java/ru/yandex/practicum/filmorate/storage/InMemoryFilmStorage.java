package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    @Getter
    private HashMap<Integer, Film> films = new HashMap<>();
    private int counterId = 1;

    @Override
    public void addFilm(Film film) {
        film.setId(counterId);
        films.put(film.getId(), film);
        counterId++;
    }

    @Override
    public void deleteFilm(Integer id) {
        if(films.containsKey(id)){
            films.remove(id);
        }
    }

    @Override
    public void upgradeFilm(Film film) {
        if(films.containsKey(film.getId())){
            films.put(film.getId(), film);
        }
    }
}
