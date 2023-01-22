package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDbStorage {

    String findNameMpa(Integer id);

    Collection<Mpa> findAll();
}
