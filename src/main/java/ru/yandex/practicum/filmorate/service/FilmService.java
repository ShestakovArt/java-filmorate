package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    final FilmDbStorage filmDbStorage;
    final MpaService mpaService;
    final GenreService genreService;
    final DirectorService directorService;

    @Autowired
    public FilmService(FilmDbStorageImpl filmDbStorage, MpaService mpaService, GenreService genreService, DirectorService directorService) {
        this.filmDbStorage = filmDbStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    public Film getFilm(Integer id) {
        return filmDbStorage.findFilm(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с идентификатором " + id + " не найден."));
    }

    public Film addFilm(Film film) {
        Integer filmId = filmDbStorage.addFilm(film);
        film.setId(filmId);
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));

        List<Genre> actualGenreFilm = executeAddFilmGenres(filmId, film.getGenres());
        film.setGenres(actualGenreFilm);

        List<Director> filmDirectors = directorService.executeAddDirectorListToFilm(filmId, film.getDirectors());
        film.setDirectors(filmDirectors);

        return film;
    }

    private List<Genre> executeAddFilmGenres(Integer filmId, List<Genre> genres) {
        List<Genre> actualGenreFilm = new ArrayList<>();
        if (genres != null) {
            for (Genre genre : genres) {
                if (!actualGenreFilm.contains(genreService.getGenre(genre.getId()))) {
                    actualGenreFilm.add(genreService.getGenre(genre.getId()));
                }
                if (!genreService.setFilmGenre(filmId, genre.getId())) {
                    log.error("can not add genres to film");
                    throw new IncorrectParameterException("Не удалось устанвоить жанр для фильма");
                }
            }
        }
        return actualGenreFilm;
    }

    public void upgradeFilm(Film film) {
        filmDbStorage.upgradeFilm(film);
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));

        List<Genre> actualGenreFilm = executeAddFilmGenres(film.getId(), film.getGenres());

        List<Genre> currentGenreFilm = genreService.getFilmGenres(film.getId());
        for (Genre current : currentGenreFilm) {
            if (!actualGenreFilm.contains(current)) {
                genreService.deleteFilmGenre(film.getId(), current.getId());
            }
        }

        film.setGenres(actualGenreFilm);

        List<Director> filmDirectors = directorService.executeAddDirectorListToFilm(film.getId(), film.getDirectors());

        List<Director> currentFilmDirectors = directorService.getFilmDirectors(film.getId());
        for (Director current : currentFilmDirectors) {
            if (!filmDirectors.contains(current)) {
                directorService.deleteFilmDirector(film.getId(), current.getId());
            }
        }

        film.setDirectors(filmDirectors);
    }

    public Collection<Film> getFilmsList() {
        return filmDbStorage.findAll();
    }

    public Collection<Film> getDirectorSortedFilms(Integer directorId, String[] sortBy) {
        Director currentDirector = directorService.getDirectorById(directorId);
        if (currentDirector != null) {
            return filmDbStorage.findDirectorSortedFilms(directorId, sortBy);
        } else {
            throw new DirectorNotFoundException("Режисер с идентификатором " + directorId + " не найден.");
        }
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        if (filmId < 1) {
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if (!filmDbStorage.addLikeFilm(filmId, userId)) {
            throw new IncorrectParameterException("Не удалось поставить лайк");
        }
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        if (filmId < 1) {
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if (!filmDbStorage.deleteLike(filmId, userId)) {
            throw new IncorrectParameterException("Не корректный запрос на удаление лайка");
        }
    }

    public List<Film> getMostPopularMoviesOfLikes(Integer count) {
        if (count < 1) {
            throw new IncorrectParameterException("Значение count должно быть больше 0");
        }

        return filmDbStorage.listMostPopularFilms(count);
    }

    public void deleteFilm(int filmId) {
        if (filmId < 1) {
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        List<Director> listFilmDirectors = directorService.getFilmDirectors(filmId);
        for (Director director : listFilmDirectors) {
            directorService.deleteFilmDirector(filmId, director.getId());
        }
        filmDbStorage.deleteFilm(filmId);
    }
}
