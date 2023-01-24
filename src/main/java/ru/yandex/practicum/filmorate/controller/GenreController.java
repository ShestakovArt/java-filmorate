package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;
    private final String pathId = "/{id}";

    @GetMapping()
    public Collection<Genre> getGenresList() {
        return genreService.getGenreList();
    }

    @GetMapping(pathId)
    public ResponseEntity<Genre> getGenre(@PathVariable int id) {
        return new ResponseEntity<>(genreService.getGenre(id), HttpStatus.OK);
    }
}
