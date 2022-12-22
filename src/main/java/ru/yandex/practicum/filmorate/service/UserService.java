package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.*;

@Service
public class UserService {
    UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorageImpl userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    private Collection<User> getUsers() {
        return userDbStorage.findAll();
    }

    public User addUser(User user) {
        user.setId(userDbStorage.addUser(user));
        return user;
    }

    public void upgradeUser(User user) {
        userDbStorage.upgradeUser(user);
    }

    public Collection<User> getUsersList(){
        return getUsers();
    }

    public boolean addFriend(Integer idUser, Integer idFriend){
        if(idUser < 1 || idFriend < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        } else {
            getUser(idUser);
            getUser(idFriend);
        }
        return addRequestsFriendship(idUser,idFriend);
    }

    public void deleteFriend(Integer idUser, Integer idFriend){
        if(idUser < 1 || idFriend < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        if(!deleteFriends(idUser, idFriend)){
            throw new IncorrectParameterException("Не удалось удалить пользователя из друзей");
        }
    }

    public List<User> getUserFriends(Integer idUser){
        if(idUser < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<Integer> idFriendsList = findAllFriends(idUser);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : idFriendsList){
            friends.add(getUser(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriend(Integer idUser, Integer idFriend){
        if(idUser < 1 || idFriend < 1){
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        List<User> commonFriend = new ArrayList<>();
        Set<Integer> common = new HashSet<>(findAllFriends(idUser));
        common.retainAll(findAllFriends(idFriend));
        for(Integer idFriendUser : common){
            commonFriend.add(getUser(idFriendUser));
        }
        return commonFriend;
    }

    public User getUser(Integer id){
        return userDbStorage.findUser(id)
                .orElseThrow(() ->new UserNotFoundException("Пользователь с идентификатором " + id + " не найден."));
    }

    private boolean addRequestsFriendship (Integer sender, Integer recipient){
        if(!findRequestsFriendship(sender, recipient)){
            HashMap<String, Integer> map = new HashMap<>();
            map.put("SENDER_ID", sender);
            map.put("RECIPIENT_ID", recipient);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FRIENDSHIP_REQUESTS")
                    .usingColumns("SENDER_ID", "RECIPIENT_ID");
            return simpleJdbcInsert.execute(map) == 1;
        }
        return false;
    }

    private boolean findRequestsFriendship(Integer firstId, Integer secondId) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where (SENDER_ID = %d or RECIPIENT_ID = %d)" +
                " and (SENDER_ID = %d or RECIPIENT_ID = %d)", firstId, firstId, secondId, secondId);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private List<Integer> findAllFriends(Integer idUser) {
        String sqlQuery = String.format("select RECIPIENT_ID as friends\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d", idUser, idUser);
        return jdbcTemplate.queryForList(sqlQuery, Integer.class);
    }

    private boolean deleteFriends(Integer idUser, Integer idFriend) {
        String sqlQuery = String.format("delete\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d and RECIPIENT_ID = %d", idUser, idFriend);
        return jdbcTemplate.update(sqlQuery) > 0;
    }
}
