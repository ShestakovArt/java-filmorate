package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorageImpl userDbStorage) {
        this.userDbStorage = userDbStorage;
    }


    private Collection<User> getUsers() {
        return userDbStorage.findAll();
    }

    public User addUser(User user) {
        user.setId(userDbStorage.addUser(user));
        return user;
    }

    public void deleteUser(Integer id) {
        userDbStorage.deleteUser(id);
    }

    public void upgradeUser(User user) {
        userDbStorage.upgradeUser(user);
    }

    public Collection<User> getUsersList(){
        return getUsers();
    }

    public void addFriend(Integer idUser, Integer idFriend){
        if(idUser < 1 || idFriend < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        user.addFriend(idFriend);
        friend.addFriend(idUser);
    }

    public void deleteFriend(Integer idUser, Integer idFriend){
        if(idUser < 1 || idFriend < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        user.deleteFriend(idFriend);
        friend.deleteFriend(idUser);
    }

    public List<User> getUserFriends(Integer idUser){
        if(idUser < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> friends = new ArrayList<>();
        for (Integer friendId : getUser(idUser).getFriends()){
            if(getUsersList().contains(getUser(friendId))){
                friends.add(getUser(friendId));
            }
        }
        return friends;
    }

    public List<User> getCommonFriend(Integer idUser, Integer idFriend){
        if(idUser < 1 || idFriend < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> commonFriend = new ArrayList<>();
        for(Integer idFriendUser : getUser(idUser).getFriends()){
            if(getUser(idFriend).getFriends().contains(idFriendUser)){
                commonFriend.add(getUser(idFriendUser));
            }
        }
        return commonFriend;
    }

    public User getUser(Integer id){
        return userDbStorage.findUser(id)
                .orElseThrow(() ->new UserNotFoundException("Пользователь с идентификатором " + id + " не найден."));
    }
}
