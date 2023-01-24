package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static ru.yandex.practicum.filmorate.enums.EventOperation.ADD;
import static ru.yandex.practicum.filmorate.enums.EventOperation.REMOVE;
import static ru.yandex.practicum.filmorate.enums.EventType.FRIEND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userDbStorage;

    public User addUser(User user) {
        user.setId(userDbStorage.addUser(user));
        return user;
    }

    public void deleteUser(Integer userId) {
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> tempUserFriends = getUserFriends(userId);
        if (tempUserFriends.size() > 0) {
            for (User user : tempUserFriends) {
                userDbStorage.deleteFriends(userId, user.getId());
            }
        }
        userDbStorage.deleteUser(userId);
    }

    public void upgradeUser(User user) {
        userDbStorage.upgradeUser(user);
    }

    public Collection<User> getUsersList() {
        return userDbStorage.findAll();
    }

    public boolean addFriend(Integer userId, Integer idFriend) {
        if (userId < 1 || idFriend < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        } else {
            getUser(userId);
            getUser(idFriend);
        }

        boolean resultOperation = userDbStorage.addRequestsFriendship(userId, idFriend);
        if (resultOperation) {
            userDbStorage.recordEvent(userId, idFriend, FRIEND, ADD);
        }

        return resultOperation;
    }

    public void deleteFriend(Integer userId, Integer idFriend) {
        if (userId < 1 || idFriend < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if (!userDbStorage.deleteFriends(userId, idFriend)) {
            throw new IncorrectParameterException("Не удалось удалить пользователя из друзей");
        } else {
            userDbStorage.recordEvent(userId, idFriend, FRIEND, REMOVE);
        }
    }

    public List<User> getUserFriends(Integer userId) {
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<Integer> idFriendsList = userDbStorage.findAllFriends(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : idFriendsList) {
            friends.add(getUser(friendId));
        }

        return friends;
    }

    public List<User> getCommonFriend(Integer userId, Integer idFriend) {
        if (userId < 1 || idFriend < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> commonFriend = new ArrayList<>();
        Set<Integer> common = new HashSet<>(userDbStorage.findAllFriends(userId));
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

    public Collection<Feed> getUserFeed(Integer userId) {
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        return userDbStorage.getFeed(userId);
    }
}