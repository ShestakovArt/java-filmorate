package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

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
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));
        List<Genre> actualGenreFilm = new ArrayList<>();
        for (Genre genre : film.getGenres()){
            actualGenreFilm.add(genreService.getGenre(genre.getId()));
            if(!filmDbStorage.setGenreFilm(film.getId(), genre.getId())) {
                throw new ValidationException("Не удалось устанвоить жанр для фильма");
            }
        }
        film.setGenres(actualGenreFilm);
        return film;
    }

    public void upgradeFilm(Film film) {
        filmDbStorage.upgradeFilm(film);
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));
        List<Genre> actualGenreFilm = new ArrayList<>();
        for (Genre genre : film.getGenres()){
            if(!actualGenreFilm.contains(genreService.getGenre(genre.getId()))){
                actualGenreFilm.add(genreService.getGenre(genre.getId()));
            }
            if(!filmDbStorage.setGenreFilm(film.getId(), genre.getId())) {
                throw new ValidationException("Не удалось устанвоить жанр для фильма");
            }
        }

        List<Genre> currentGenreFilm = filmDbStorage.getGenresFilm(film.getId());
        for(Genre current : currentGenreFilm){
            if(!actualGenreFilm.contains(current)){
                filmDbStorage.deleteGenreFilm(film.getId(), current.getId());
            }
        }
        film.setGenres(actualGenreFilm);
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
        if(!filmDbStorage.addLikeFilm(filmId, userId)) {
            throw new ValidationException("Не удалось поставить лайк");
        }
    }

    public void deleteLikeFilm(Integer idFilm, Integer idUser){
        if(idFilm < 1){
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if(idUser < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if(!filmDbStorage.deleteLike(idFilm, idUser)) {
            throw new ValidationException("Не корректный запрос на удаление лайка");
        }
    }

    public List<Film> getMostPopularMoviesOfLikes(Integer count){
        if(count < 1){
            throw new IncorrectParameterException("Значение count должно быть больше 0");
        }
        return filmDbStorage.listMostPopularFilms(count);
    }
}
