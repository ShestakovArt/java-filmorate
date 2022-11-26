package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public HashMap<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }

    public void upgradeUser(User user) {
        userStorage.upgradeUser(user);
    }

    public Collection<User> getUsersList(){
        return userStorage.getUsers().values();
    }

    public void addFriend(Integer idUser, Integer idFriend){
        if(idUser > 0 && idFriend > 0){
            userStorage.getUsers().get(idUser).addFriend(idFriend);
            userStorage.getUsers().get(Math.toIntExact(idFriend)).addFriend(idUser);
        }
    }

    public void deleteFriend(Integer idUser, Integer idFriend){
        if(idUser > 0 && idFriend > 0){
            userStorage.getUsers().get(idUser).deleteFriend(idFriend);
            userStorage.getUsers().get(Math.toIntExact(idFriend)).deleteFriend(idUser);
        }
    }

    public List<User> getUserFriends(Integer userId){
        List<User> friends = new ArrayList<>();
        for (Integer friendId : userStorage.getUsers().get(userId).getFriends()){
            if(userStorage.getUsers().containsKey(friendId)){
                friends.add(userStorage.getUsers().get(friendId));
            }
        }
        return friends;
    }

    public List<User> getCommonFriend(Integer idUser, Integer idFriend){
        List<User> commonFriend = new ArrayList<>();
        if(idUser > 0 && idFriend > 0){
            for(Integer idFriendUser : userStorage.getUsers().get(idUser).getFriends()){
                if(userStorage.getUsers().get(idFriend).getFriends().contains(idFriendUser)){
                    commonFriend.add(userStorage.getUsers().get(idFriendUser));
                }
            }
        }
        return commonFriend;
    }
}
