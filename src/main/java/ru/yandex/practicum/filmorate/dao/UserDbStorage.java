package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Optional;

public interface UserDbStorage {
    Optional<User> addUser(User user);
    void deleteUser(Integer id);
    void upgradeUser(User user);
}
