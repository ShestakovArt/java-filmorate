package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    void addUser(User user);
    void deleteUser(Integer id);
    void upgradeUser(User user);
}
