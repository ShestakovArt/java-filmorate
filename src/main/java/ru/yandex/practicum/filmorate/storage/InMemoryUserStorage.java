package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Integer, User> users = new HashMap<>();
    private int counterId = 1;

    @Override
    public HashMap<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User addUser(User user) {
        user.setId(counterId);
        users.put(user.getId(), user);
        counterId++;
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
        }
    }

    @Override
    public void upgradeUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
    }
}
