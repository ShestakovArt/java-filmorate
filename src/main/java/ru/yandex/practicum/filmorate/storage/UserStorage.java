package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Component
public interface UserStorage {
    HashMap<Integer, User> getUsers();
    User addUser(User user);
    void deleteUser(Integer id);
    void upgradeUser(User user);
}
