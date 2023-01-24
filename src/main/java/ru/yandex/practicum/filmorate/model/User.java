package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.BirthdayValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

@Data
@BirthdayValid
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Integer id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @")
    String email;
    @NotEmpty(message = "Логин не может быть пустым и содержать пробелы")
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    String login;
    String name;
    String birthday;

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        if (name == null || name.isEmpty() || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_EMAIL", email);
        values.put("USER_LOGIN", login);
        values.put("USER_NAME", name);
        values.put("USER_BIRTHDAY", birthday);

        return values;
    }
}
