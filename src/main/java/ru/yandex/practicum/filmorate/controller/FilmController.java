package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping()
    public List<Film> getFilms(){
        List<Film> filmList =new ArrayList<>();
        for (Film film : films.values()){
            filmList.add(film);
        }
        return filmList;
    }

    @PostMapping()
    public Film create(@RequestBody Film film){
        checkValidateDataFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping()
    public Film update(@RequestBody Film film){
        checkValidateDataFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private void checkValidateDataFilm(Film film){
        try{
            DateTimeFormatter releaseDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if(film.getName().isEmpty() || film.getName() == null){
                throw new ValidationException("Название не может быть пустым");
            }
            if(film.getDescription().length() > 200){
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            if(LocalDate.parse(film.getReleaseDate(), releaseDate)
                    .isBefore(LocalDate.of(1895, 12, 27))){
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895");
            }
            if(film.getDuration() < 0){
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
        }
        catch (ValidationException e){
            System.out.println(e.getMessage());
        }
    }
}
