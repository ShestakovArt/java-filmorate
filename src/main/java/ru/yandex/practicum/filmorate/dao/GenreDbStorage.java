package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Component
public interface GenreDbStorage {
    String findNameGenre(Integer id);
    Collection<Genre> findAll();
}
