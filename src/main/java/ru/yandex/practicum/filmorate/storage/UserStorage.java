package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {

    HashMap<Integer, User> getUsers();

    User addUser(User user);

    void deleteUser(Integer id);

    void upgradeUser(User user);
}
