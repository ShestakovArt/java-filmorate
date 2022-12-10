package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.BirthdayValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Data
@BirthdayValid
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User {
    int id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @")
    String email;
    @NotEmpty(message = "Логин не может быть пустым и содержать пробелы")
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    String login;
    String name;
    String birthday;
    Set<Integer> friends = new HashSet<>();

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        if(name == null || name.isEmpty() || name.isBlank()){
            this.name = login;
        } else{
            this.name = name;
        }
        this.birthday = birthday;
    }

    public void addFriend(Integer idFriend){
        if (idFriend > 0){
            friends.add(idFriend);
        }
    }

    public void deleteFriend(Integer idFriend){
        if (idFriend > 0){
            friends.remove(idFriend);
        }
    }
}
