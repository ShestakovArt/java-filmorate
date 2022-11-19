package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private int counterId = 1;

    @GetMapping()
    public List<Film> getFilms(){
        List<Film> filmList =new ArrayList<>();
        for (Film film : films.values()){
            filmList.add(film);
        }
        return filmList;
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film){
        film.setId(counterId);
        films.put(film.getId(), film);
        counterId++;
        return film;
    }

    @PutMapping()
    public ResponseEntity<Film> update(@Valid @RequestBody @NotNull Film film){
        ResponseEntity<Film> tResponseEntity = new ResponseEntity<>(film, HttpStatus.NOT_FOUND);;
        if(films.containsKey(film.getId())){
            films.put(film.getId(), film);
            tResponseEntity = new ResponseEntity<>(film, HttpStatus.OK);
        }
        return tResponseEntity;
    }
}
