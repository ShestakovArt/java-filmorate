package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.*;

@Service
public class UserService {
    final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorageImpl userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User addUser(User user) {
        user.setId(userDbStorage.addUser(user));

        return user;
    }

    public void deleteUser(Integer idUser) {
        if (idUser < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> tempUserFriends = getUserFriends(idUser);
        if (tempUserFriends.size() > 0) {
            for (User user : tempUserFriends) {
                deleteFriend(idUser, user.getId());
            }
        }
        userDbStorage.deleteUser(idUser);
    }

    public void upgradeUser(User user) {
        userDbStorage.upgradeUser(user);
    }

    public Collection<User> getUsersList() {
        return userDbStorage.findAll();
    }

    public boolean addFriend(Integer idUser, Integer idFriend) {
        if (idUser < 1 || idFriend < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        } else {
            getUser(idUser);
            getUser(idFriend);
        }

        return userDbStorage.addRequestsFriendship(idUser, idFriend);
    }

    public void deleteFriend(Integer idUser, Integer idFriend) {
        if (idUser < 1 || idFriend < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if (!userDbStorage.deleteFriends(idUser, idFriend)) {
            throw new IncorrectParameterException("Не удалось удалить пользователя из друзей");
        }
    }

    public List<User> getUserFriends(Integer idUser) {
        if (idUser < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<Integer> idFriendsList = userDbStorage.findAllFriends(idUser);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : idFriendsList) {
            friends.add(getUser(friendId));
        }

        return friends;
    }

    public List<User> getCommonFriend(Integer idUser, Integer idFriend) {
        if (idUser < 1 || idFriend < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> commonFriend = new ArrayList<>();
        Set<Integer> common = new HashSet<>(userDbStorage.findAllFriends(idUser));
        common.retainAll(userDbStorage.findAllFriends(idFriend));
        for (Integer idFriendUser : common) {
            commonFriend.add(getUser(idFriendUser));
        }

        return commonFriend;
    }

    public User getUser(Integer id) {
        return userDbStorage.findUser(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + id + " не найден."));
    }
}
