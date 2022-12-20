package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


@Component
public interface UserDbStorage {
    int addUser(User user);
    boolean deleteUser(Integer id);
    void upgradeUser(User user);
    Optional<User> findUser(Integer id);
    Collection<User> findAll();
}
