package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        ResponseEntity<Film> tResponseEntity = new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        if(filmService.getFilms().containsKey(film.getId())){
            filmService.upgradeFilm(film);
            tResponseEntity = new ResponseEntity<>(film, HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getUser(@PathVariable int id){
        ResponseEntity<Film> tResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(filmService.getFilms().containsKey(id)){
            tResponseEntity = new ResponseEntity<>(filmService.getFilms().get(id), HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @GetMapping("/popular")
    public LinkedList<Film> getPopularFilms(@RequestParam(required = false) Integer count){
        if (count == null){
            count = 10;
        }
        return filmService.getMostPopularMoviesOfLikes(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> putLikeFilm(@PathVariable Integer id, @PathVariable Integer userId){
        ResponseEntity<Film> tResponseEntity = new ResponseEntity<>(filmService.getFilms().get(id), HttpStatus.NOT_FOUND);
        if(filmService.getFilms().containsKey(id) && filmService.getFilms().containsKey(userId)){
            filmService.addLikeFilm(id, userId);
            tResponseEntity = new ResponseEntity<>(filmService.getFilms().get(id), HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLikeFilm(@PathVariable Integer id, @PathVariable Integer userId){
        ResponseEntity<Film> tResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(filmService.getFilms().containsKey(id) && filmService.getFilms().containsKey(userId)){
            filmService.deleteLikeFilm(id, userId);
            tResponseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return tResponseEntity;
    }
}
