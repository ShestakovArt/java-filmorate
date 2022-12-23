package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
public class MpaService {
    MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage){
        this.mpaDbStorage = mpaDbStorage;
    }

    public Collection<Mpa> getMpaList(){
        return mpaDbStorage.findAll();
    }

    public Mpa getMpa(Integer id){
        return new Mpa(id, mpaDbStorage.findNameMpa(id));
    }
}
