package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.SearchParamsValid;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final String pathId = "/{id}";
    private final String pathLikeFilm = pathId + "/like/{userId}";
    private static final String DEFAULT_POPULAR_COUNT = "10";

    @GetMapping()
    public Collection<Film> getFilms() {
        return filmService.getFilmsList();
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilms(@PathVariable Integer directorId, @RequestParam String[] sortBy) {
        return filmService.getDirectorSortedFilms(directorId, sortBy);
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping()
    public ResponseEntity<Film> update(@Valid @RequestBody @NotNull Film film) {
        boolean findFlag = false;
        for (Film equredFilm : filmService.getFilmsList()) {
            if (equredFilm.getId() == film.getId()) {
                findFlag = true;
            }
        }
        if (!findFlag) {
            throw new FilmNotFoundException("Нет фильма с таким ID");
        }
        filmService.upgradeFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping(pathId)
    public ResponseEntity<Film> getFilm(@PathVariable int id) {
        return new ResponseEntity<>(filmService.getFilm(id), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(required = false, defaultValue = DEFAULT_POPULAR_COUNT) Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return new ResponseEntity<>(filmService.getMostPopularMoviesOfLikes(count, genreId, year), HttpStatus.OK);
    }

    @PutMapping(pathLikeFilm)
    public ResponseEntity putLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLikeFilm(id, userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(pathLikeFilm)
    public ResponseEntity deleteLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLikeFilm(id, userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(
            @RequestParam("query") @NotNull @NotEmpty String criteria,
            @RequestParam("by") @SearchParamsValid Set<String> params
    ) {
        return filmService.findFilmsByCriteria(criteria, params);
    }

    @DeleteMapping(pathId)
    public ResponseEntity<Film> deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam Integer userId,
            @RequestParam Integer friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
