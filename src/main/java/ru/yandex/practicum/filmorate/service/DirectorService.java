package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    public Director add(Director director) {
        director.setId(directorDbStorage.add(director));
        return director;
    }

    public List<Director> executeAddDirectorListToFilm(Integer filmId, List<Director> directors) {
        List<Director> filmDirectors = new ArrayList<>();
        if (directors != null) {
            for (Director director : directors) {
                Director filmDirector = executeAddDirectorToFilm(filmId, director.getId());
                filmDirectors.add(filmDirector);
            }
        }
        return filmDirectors;
    }

    private Director executeAddDirectorToFilm(Integer filmId, Integer directorId) {
        Optional<Director> directorOptional = directorDbStorage.findById(directorId);
        if (!directorOptional.isEmpty()) {
            directorDbStorage.addFilmDirector(filmId, directorId);
            return directorOptional.get();
        } else {
            log.error("director not exists");
            throw new IncorrectParameterException("Не удалось устанвоить режисера для фильма");
        }
    }

    public Collection<Director> getDirectors() {
        return directorDbStorage.findAll();
    }

    public Director getDirectorById(int id) {
        Optional<Director> directorOptional = directorDbStorage.findById(id);
        if (!directorOptional.isEmpty()) {
            return directorOptional.get();
        } else {
            throw new DirectorNotFoundException("Режисер с идентификатором " + id + " не найден.");
        }
    }

    public Director update(Director director) {
        directorDbStorage.updateDirector(director);
        return directorDbStorage.findById(director.getId()).get();
    }

    public void deleteDirector(int id) {
        Optional<Director> existsDirector = directorDbStorage.findById(id);
        if (!existsDirector.isEmpty()) {
            directorDbStorage.deleteDirector(id);
        }
    }

    public void deleteFilmDirector(Integer filmId, Integer directorId) {
        directorDbStorage.deleteFilmDirector(filmId, directorId);
    }

    public List<Director> getFilmDirectors(Integer filmId) {
        return directorDbStorage.getFilmDirectors(filmId);
    }
}
