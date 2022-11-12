package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    @GetMapping()
    public List<User> getUsers(){
        return users;
    }

    @PostMapping("/user")
    public User create(@RequestBody User user){
        checkValidateDataUser(user);
        users.add(user);
        return user;
    }

    @PutMapping("/user/{id}")
    public User update(@PathVariable(name = "id") int id, @RequestBody User user){
        checkValidateDataUser(user);
        users.set(id, user);
        return user;
    }

    private void checkValidateDataUser(User user){
        try{
            DateTimeFormatter releaseDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            if(user.getEmail().isEmpty() || user.getEmail() == null){
                throw new ValidationException("Электронная почта не может быть пустой");
            }
            if (user.getEmail().matches("\\w+@\\w+\\.\\w+")){
                throw new ValidationException("Электронная почта должна содержать символ @");
            }
            if(user.getLogin().isEmpty() || user.getLogin() == null || user.getLogin().isBlank()){
                throw new ValidationException("Логин не может быть пустым");
            }
            if(user.getLogin().length() > 0){
                if (user.getLogin().contains(" ")){
                    throw new ValidationException("Логин не может содержать пробелы");
                }
            }
            if(user.getName().isEmpty() || user.getName() == null || user.getName().isBlank()){
                user.setName(user.getLogin());
            }
            if(LocalDate.parse(user.getBirthday(), releaseDate).isAfter(LocalDate.now())){
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        }
        catch (ValidationException e){
            System.out.println(e.getMessage());
        }
    }
}
