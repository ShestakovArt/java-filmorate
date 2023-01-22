package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    final GenreDbStorage genreDbStorage;

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
