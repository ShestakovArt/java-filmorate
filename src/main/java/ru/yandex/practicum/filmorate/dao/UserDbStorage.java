package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserDbStorage {

    int addUser(User user);

    void upgradeUser(User user);

    Optional<User> findUser(Integer id);

    Collection<User> findAll();

    boolean addRequestsFriendship(Integer userId, Integer idFriend);

    boolean deleteFriends(Integer userId, Integer idFriend);

    boolean deleteUser(Integer userId);

    List<Integer> findAllFriends(Integer userId);

    Collection<Feed> getFeed(Integer userId);

    void recordEvent(Integer userId, Integer entityId, EventType type, EventOperation operation);
}
