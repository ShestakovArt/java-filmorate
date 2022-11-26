package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public Collection<Film> getFilms(){
        return filmService.getFilmsList();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film){
        return filmService.addFilm(film);
    }

    @PutMapping()
    public ResponseEntity<Film> update(@Valid @RequestBody @NotNull Film film){
        if(!filmService.getFilms().containsKey(film.getId())){
            throw new FilmNotFoundException("Нет фильма с таким ID");
        }
        filmService.upgradeFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable int id){
        if(!filmService.getFilms().containsKey(id)){
            throw new FilmNotFoundException("Нет фильма с таким ID");
        }
        return new ResponseEntity<>(filmService.getFilms().get(id), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count){
        if (count == null){
            count = 10;
        }
        return filmService.getMostPopularMoviesOfLikes(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> putLikeFilm(@PathVariable Integer id, @PathVariable Integer userId){
        if(!filmService.getFilms().containsKey(id)){
            throw new FilmNotFoundException("Нет фильма с таким ID");
        }
        filmService.addLikeFilm(id, userId);
        return new ResponseEntity<>(filmService.getFilms().get(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLikeFilm(@PathVariable Integer id, @PathVariable Integer userId){
        if(!filmService.getFilms().containsKey(id)){
            throw new FilmNotFoundException("Нет фильма с таким ID");
        }
        filmService.deleteLikeFilm(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
