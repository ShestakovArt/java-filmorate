package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
public interface MpaDbStorage {
    String findNameMpa(Integer id);
    Collection<Mpa> findAll();
}
