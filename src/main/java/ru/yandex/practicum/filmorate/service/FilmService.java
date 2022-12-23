package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmDbStorage filmDbStorage;
    final MpaService mpaService;
    final GenreService genreService;

    @Autowired
    public FilmService(FilmDbStorageImpl filmDbStorage, MpaService mpaService, GenreService genreService) {
        this.filmDbStorage = filmDbStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film getFilm(Integer id){
        return filmDbStorage.findFilm(id)
                .orElseThrow(() ->new FilmNotFoundException("Фильм с идентификатором " + id + " не найден."));
    }

    public Film addFilm(Film film) {
        film.setId(filmDbStorage.addFilm(film));
        filmSetMpaAndGenre(film);
        return film;
    }

    public void deleteFilm(Integer id) {
        filmDbStorage.deleteFilm(id);
    }

    public void upgradeFilm(Film film) {
        filmDbStorage.upgradeFilm(film);
        filmSetMpaAndGenre(film);
    }

    public Collection<Film> getFilmsList(){
        return filmDbStorage.findAll();
    }

    public void addLikeFilm(Integer filmId, Integer userId){
        if(filmId < 1){
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if(userId < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
//        if (!filmDbStorage.getFilms().get(filmId).getLikes().contains(userId)){
//            filmDbStorage.getFilms().get(filmId).addLike(userId);
//        }
    }

    public void deleteLikeFilm(Integer filmId, Integer userId){
        if(filmId < 1){
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if(userId < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        //filmDbStorage.getFilms().get(filmId).deleteLike(userId);
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

    private void filmSetMpaAndGenre(Film film){
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));
        List<Genre> actualGenreFilm = new ArrayList<>();
        for (Genre genre : film.getGenres()){
            actualGenreFilm.add(genreService.getGenre(genre.getId()));
            filmDbStorage.setGenreFilm(film.getId(), genre.getId());
        }
        film.setGenres(actualGenreFilm);
    }
}
