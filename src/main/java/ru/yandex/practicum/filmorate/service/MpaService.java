package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Collection<Mpa> getMpaList() {
        return mpaDbStorage.findAll();
    }

    public Mpa getMpa(Integer id) {
        return new Mpa(id, mpaDbStorage.findNameMpa(id));
    }
}
