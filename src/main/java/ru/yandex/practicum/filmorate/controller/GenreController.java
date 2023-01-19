package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    final GenreService genreService;
    final String pathId = "/{id}";

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping()
    public Collection<Genre> getGenresList() {
        return genreService.getGenreList();
    }

    @GetMapping(pathId)
    public ResponseEntity<Genre> getGenre(@PathVariable int id) {
        return new ResponseEntity<>(genreService.getGenre(id), HttpStatus.OK);
    }
}
