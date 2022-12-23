package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
    final String pathId = "/{id}";
    final String pathLikeFilm = pathId + "/like/{userId}";

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
        boolean findFlag = false;
        for (Film equredUser : filmService.getFilmsList()){
            if(equredUser.getId() == film.getId()){
                findFlag = true;
            }
        }
        if(!findFlag){
            throw new FilmNotFoundException("Нет фильма с таким ID");
        }
        filmService.upgradeFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping(pathId)
    public ResponseEntity<Film> getFilm(@PathVariable int id){
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(filmService.getFilm(id), HttpStatus.OK);
        }
        catch (FilmNotFoundException | EmptyResultDataAccessException e){
            response = new ResponseEntity<>("Нет фильма с таким ID", HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count){
        if (count == null){
            count = 10;
        }
        return filmService.getMostPopularMoviesOfLikes(count);
    }

    @PutMapping(pathLikeFilm)
    public ResponseEntity<Film> putLikeFilm(@PathVariable Integer id, @PathVariable Integer userId){
        filmService.addLikeFilm(id, userId);
        return new ResponseEntity<>(filmService.getFilm(id), HttpStatus.OK);
    }

    @DeleteMapping(pathLikeFilm)
    public ResponseEntity<Film> deleteLikeFilm(@PathVariable Integer id, @PathVariable Integer userId){
        filmService.deleteLikeFilm(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
