package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public HashMap<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public void upgradeFilm(Film film) {
        filmStorage.upgradeFilm(film);
    }

    public Collection<Film> getFilmsList(){
        return filmStorage.getFilms().values();
    }

    public void addLikeFilm(Integer filmId, Integer userId){
        if(filmId < 1){
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if(userId < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if (!filmStorage.getFilms().get(filmId).getLikes().contains(userId)){
            filmStorage.getFilms().get(filmId).addLike(userId);
        }
    }

    public void deleteLikeFilm(Integer filmId, Integer userId){
        if(filmId < 1){
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if(userId < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        filmStorage.getFilms().get(filmId).deleteLike(userId);
    }

    public List<Film> getMostPopularMoviesOfLikes(Integer count){
        if(count < 1){
            throw new IncorrectParameterException("Значение count должно быть больше 0");
        }
        Comparator<Film> filmComparator = (film1, film2) -> {
            if(film2.getRate().compareTo(film1.getRate()) == 0){
                return film1.getName().compareTo(film2.getName());
            }
            return film2.getRate().compareTo(film1.getRate());
        };
        return getFilmsList().stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
