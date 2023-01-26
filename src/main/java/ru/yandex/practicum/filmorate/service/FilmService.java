package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.enums.EventOperation.ADD;
import static ru.yandex.practicum.filmorate.enums.EventOperation.REMOVE;
import static ru.yandex.practicum.filmorate.enums.EventType.LIKE;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final UserDbStorage userDbStorage;
    private final LikeService likeService;

    private static final Comparator<Film> filmPopularityComparator = Comparator.comparing(Film::getRate).reversed();
    private static final int EARLIEST_RELEASE_DATE = 1895;

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
        if (!likeService.addLikeFilm(filmId, userId)) {
            throw new IncorrectParameterException("Не удалось поставить лайк");
        } else {
            userDbStorage.recordEvent(userId, filmId, LIKE, ADD);
        }
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        if (filmId < 1) {
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if (!likeService.deleteLike(filmId, userId)) {
            throw new IncorrectParameterException("Не корректный запрос на удаление лайка");
        } else {
            userDbStorage.recordEvent(userId, filmId, LIKE, REMOVE);
        }
    }

    public List<Film> getMostPopularMoviesOfLikes(Integer count, Integer genreId, Integer year) {
        if (genreId != null) {
            genreService.getGenre(genreId);
        }
        if (year != null) {
            if (year < EARLIEST_RELEASE_DATE || year > LocalDate.now().getYear()) {
                throw new IncorrectParameterException("Year must be any int from 1895 till current year or absent.");
            }
        }
        return filmDbStorage.listMostPopularFilms(count, genreId, year);
    }

    public Collection<Film> findFilmsByCriteria(String criteria, Set<String> params) {
        Collection<Film> foundFilms = new TreeSet<>(filmPopularityComparator);
        if (params.contains("director")) {
            foundFilms.addAll(filmDbStorage.findFilmsByDirector(criteria));
        }
        if (params.contains("title")) {
            foundFilms.addAll(filmDbStorage.findFilmsByTitle(criteria));
        }
        return foundFilms;
    }

    public void deleteFilm(int filmId) {
        if (filmId < 1) {
            throw new FilmNotFoundException("Id фильма должно быть больше 0");
        }
        List<Director> listFilmDirectors = directorService.getFilmDirectors(filmId);
        for (Director director : listFilmDirectors) {
            directorService.deleteFilmDirector(filmId, director.getId());
        }
        likeService.deleteAllLikes(filmId);
        filmDbStorage.deleteFilm(filmId);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userService.getUser(userId);
        userService.getUser(friendId);
        return filmDbStorage.getCommonFilms(userId, friendId);
    }
}
