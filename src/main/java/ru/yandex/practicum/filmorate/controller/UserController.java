package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    final String pathIdFriend = pathFriends + "/{friendId}";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public Collection<User> getUsers(){
        return userService.getUsersList();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user){
        return userService.addUser(user);
    }

    @PutMapping()
    public ResponseEntity<User> update(@Valid @RequestBody @NotNull User user){
        boolean findFlag = false;
        for (User equredUser : userService.getUsersList()){
            if(equredUser.getId() == user.getId()){
                findFlag = true;
            }
        }
        if(!findFlag){
            throw new UserNotFoundException("Нет пользователя с таким ID");
        }
        userService.upgradeUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(pathId)
    public ResponseEntity<User> getUser(@PathVariable int id){
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
        }
        catch (UserNotFoundException | EmptyResultDataAccessException e){
            response = new ResponseEntity<>("Нет пользователя с таким ID", HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PutMapping(pathIdFriend)
    public ResponseEntity<User> putUserFriend(@PathVariable Integer id, @PathVariable Integer friendId){
        ResponseEntity response;
        try {
            if(userService.addFriend(id, friendId)) {
                response = new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
            } else {
                throw new ValidationException("Не удалось добавить пользователя в друзья");
            }
        }
        catch (UserNotFoundException | ValidationException | EmptyResultDataAccessException e){
            response = new ResponseEntity<>(userService.getUser(id), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @DeleteMapping(pathIdFriend)
    public ResponseEntity<User> deleteUserFriend(@PathVariable Integer id, @PathVariable Integer friendId){
        if(!userService.getUsersList().contains(id) || !userService.getUsersList().contains(friendId)){
            throw new UserNotFoundException("Нет пользователя с таким ID");
        }
        userService.deleteFriend(id, friendId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(pathFriends)
    public ResponseEntity<List<User>> getUserFriends(@PathVariable Integer id){
        ResponseEntity response;
        try {
            userService.getUser(id);
            response = new ResponseEntity<>(userService.getUserFriends(id), HttpStatus.OK);
        }
        catch (UserNotFoundException | ValidationException | EmptyResultDataAccessException e){
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @GetMapping(pathFriends + "/common/{otherId}")
    public ResponseEntity<List<User>> getCommonUsersFriends(@PathVariable Integer id, @PathVariable Integer otherId){
        if(!userService.getUsersList().contains(id) || !userService.getUsersList().contains(otherId)){
            throw new UserNotFoundException("Нет пользователя с таким ID");
        }
        return new ResponseEntity<>(userService.getCommonFriend(id, otherId), HttpStatus.OK);
    }
}
