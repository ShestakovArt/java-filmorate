package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorDbStorage {

    int add(Director director);

    Collection<Director> findAll();

    Optional<Director> findById(Integer id);

    void updateDirector(Director director);

    void deleteDirector(int directorId);

    void deleteFilmDirector(Integer filmId, Integer directorId);

    void addFilmDirector(Integer filmId, Integer directorId);

    List<Director> getFilmDirectors(Integer filmId);
}
