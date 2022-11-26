package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        ResponseEntity<User> tResponseEntity = new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        if(userService.getUsers().containsKey(user.getId())){
            userService.upgradeUser(user);
            tResponseEntity = new ResponseEntity<>(user, HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id){
        ResponseEntity<User> tResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(userService.getUsers().containsKey(id)){
            tResponseEntity = new ResponseEntity<>(userService.getUsers().get(id), HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> putUserFriend(@PathVariable Integer id, @PathVariable Integer friendId){
        ResponseEntity<User> tResponseEntity = new ResponseEntity<>(userService.getUsers().get(id), HttpStatus.NOT_FOUND);
        if(userService.getUsers().containsKey(id) && userService.getUsers().containsKey(friendId)){
            userService.addFriend(id, friendId);
            tResponseEntity = new ResponseEntity<>(userService.getUsers().get(id), HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteUserFriend(@PathVariable Integer id, @PathVariable Integer friendId){
        ResponseEntity<User> tResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(userService.getUsers().containsKey(id) && userService.getUsers().containsKey(friendId)){
            userService.deleteFriend(id, friendId);
            tResponseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return tResponseEntity;
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getUserFriends(@PathVariable Integer id){
        ResponseEntity<List<User>> tResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(userService.getUsers().containsKey(id)){
            tResponseEntity = new ResponseEntity<>(userService.getUserFriends(id), HttpStatus.OK);
        }
        return tResponseEntity;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonUsersFriends(@PathVariable Integer id, @PathVariable Integer otherId){
        ResponseEntity<List<User>> tResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if(userService.getUsers().containsKey(id) && userService.getUsers().containsKey(otherId)){
            tResponseEntity = new ResponseEntity<>(userService.getCommonFriend(id, otherId), HttpStatus.OK);
        }
        return tResponseEntity;
    }
}
