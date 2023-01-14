package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorageImpl;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GenreService {
    final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorageImpl genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Genre> getGenreList() {
        return genreDbStorage.findAll();
    }

    public Genre getGenre(Integer id) {
        return new Genre(id, genreDbStorage.findNameGenre(id));
    }

    public boolean setFilmGenre(Integer idFilm, Integer idGenre) {
        return genreDbStorage.setFilmGenre(idFilm, idGenre);
    }

    public boolean deleteFilmGenre(Integer idFilm, Integer idGenre) {
        return genreDbStorage.deleteFilmGenre(idFilm, idGenre);
    }

    public List<Genre> getFilmGenres(Integer idFilm) {
        return genreDbStorage.getFilmGenres(idFilm);
    }
}
