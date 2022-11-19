package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    private int counterId = 1;

    @GetMapping()
    public List<User> getUsers(){
        List<User> userList =new ArrayList<>();
        for (User user : users.values()){
            userList.add(user);
        }
        return userList;
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user){
        user.setId(counterId);
        users.put(user.getId(), user);
        counterId++;
        return user;
    }

    @PutMapping()
    public ResponseEntity<User> update(@Valid @RequestBody @NotNull User user){
        ResponseEntity<User> tResponseEntity = new ResponseEntity<>(user, HttpStatus.NOT_FOUND);;
        if(users.containsKey(user.getId())){
            users.put(user.getId(), user);
            tResponseEntity = new ResponseEntity<>(user, HttpStatus.OK);
        }
        return tResponseEntity;
    }
}
