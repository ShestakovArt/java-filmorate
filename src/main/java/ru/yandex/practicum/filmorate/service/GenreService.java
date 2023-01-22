package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorageImpl;
import ru.yandex.practicum.filmorate.model.Genre;

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
        return genreDbStorage.findGenreById(id);
    }

    public boolean setFilmGenre(Integer filmId, Integer idGenre) {
        return genreDbStorage.setFilmGenre(filmId, idGenre);
    }

    public boolean deleteFilmGenre(Integer filmId, Integer idGenre) {
        return genreDbStorage.deleteFilmGenre(filmId, idGenre);
    }

    public List<Genre> getFilmGenres(Integer filmId) {
        return genreDbStorage.getFilmGenres(filmId);
    }
}
