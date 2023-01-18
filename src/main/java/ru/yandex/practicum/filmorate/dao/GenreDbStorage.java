package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Component
public interface GenreDbStorage {
    Genre findGenreById(Integer id);

    Collection<Genre> findAll();

    List<Genre> getFilmGenres(Integer filmId);

    boolean deleteFilmGenre(Integer filmId, Integer idGenre);

    boolean setFilmGenre(Integer filmId, Integer idGenre);
}
