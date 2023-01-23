package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;
    private final String pathId = "/{id}";

    @GetMapping()
    public Collection<Mpa> getMpaList() {
        return mpaService.getMpaList();
    }

    @GetMapping(pathId)
    public ResponseEntity<Mpa> getMpa(@PathVariable int id) {
        return new ResponseEntity<>(mpaService.getMpa(id), HttpStatus.OK);
    }
}
