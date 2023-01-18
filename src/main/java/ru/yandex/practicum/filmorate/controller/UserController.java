package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    final UserService userService;
    final String pathId = "/{id}";
    final String pathFriends = pathId + "/friends";
    final String pathRecommendations = pathId + "/recommendations";
    final String pathIdFriend = pathFriends + "/{friendId}";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public Collection<User> getUsers() {
        return userService.getUsersList();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping()
    public ResponseEntity<User> update(@Valid @RequestBody @NotNull User user) {
        boolean findFlag = false;
        for (User equredUser : userService.getUsersList()) {
            if (equredUser.getId() == user.getId()) {
                findFlag = true;
            }
        }
        if (!findFlag) {
            throw new UserNotFoundException("Нет пользователя с таким ID");
        }
        userService.upgradeUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(pathId)
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PutMapping(pathIdFriend)
    public ResponseEntity<User> putUserFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        ResponseEntity response;
        if (userService.addFriend(id, friendId)) {
            response = new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        } else {
            throw new IncorrectParameterException("Не удалось добавить пользователя в друзья");
        }
        return response;
    }

    @DeleteMapping(pathIdFriend)
    public ResponseEntity<User> deleteUserFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.getUser(id);
        userService.getUser(friendId);
        userService.deleteFriend(id, friendId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(pathFriends)
    public ResponseEntity<List<User>> getUserFriends(@PathVariable Integer id) {
        userService.getUser(id);
        return new ResponseEntity<>(userService.getUserFriends(id), HttpStatus.OK);
    }

    @GetMapping(pathFriends + "/common/{otherId}")
    public ResponseEntity<List<User>> getCommonUsersFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        userService.getUser(id);
        userService.getUser(otherId);
        return new ResponseEntity<>(userService.getCommonFriend(id, otherId), HttpStatus.OK);
    }

    @DeleteMapping(pathId)
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(pathRecommendations)
    public ResponseEntity<List<Film>> getRecommendations(@PathVariable Integer id) {
        userService.getUser(id);
        return new ResponseEntity<>(userService.getRecommendations(id), HttpStatus.OK);
    }
}
