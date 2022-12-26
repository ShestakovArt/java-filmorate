package ru.yandex.practicum.filmorate.validateTests;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserTests {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Test
    public void correctlyCreatedUserTest(){
        User user = new User("testUser@yandex.ru",
                "User",
                "Test",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Объект user создан не корректно");
    }

    @Test
    public void emptyEmailUserTest(){
        User user = new User("",
                "User",
                "Test",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> userConstraintViolation : violations){
            assertTrue(userConstraintViolation.getMessageTemplate().equals("Электронная почта не может быть пустой"),
                    "Отсутствует сообщение 'Электронная почта не может быть пустой'");
        }
    }

    @Test
    public void noAtEmailUserTest(){
        User user = new User("testUseryandex.ru",
                "User",
                "Test",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> userConstraintViolation : violations){
            assertTrue(userConstraintViolation.getMessageTemplate().equals("Электронная почта должна содержать символ @"),
                    "Отсутствует сообщение 'Электронная почта должна содержать символ @'");
        }
    }

    @Test
    public void emptyLoginUserTest(){
        User user = new User("testUser@yandex.ru",
                "",
                "Test",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> userConstraintViolation : violations){
            assertTrue(userConstraintViolation.getMessageTemplate().equals("Логин не может быть пустым и содержать пробелы"),
                    "Отсутствует сообщение 'Логин не может быть пустым и содержать пробелы'");
        }
    }

    @Test
    public void whitespaceLoginUserTest(){
        User user = new User("testUser@yandex.ru",
                " ",
                "Test",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> userConstraintViolation : violations){
            assertTrue(userConstraintViolation.getMessageTemplate().equals("Логин не может быть пустым и содержать пробелы"),
                    "Отсутствует сообщение 'Логин не может быть пустым и содержать пробелы'");
        }
    }

    @Test
    public void containsWhitespaceLoginUserTest(){
        User user = new User("testUser@yandex.ru",
                "User Test",
                "Test",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> userConstraintViolation : violations){
            assertTrue(userConstraintViolation.getMessageTemplate().equals("Логин не может быть пустым и содержать пробелы"),
                    "Отсутствует сообщение 'Логин не может быть пустым и содержать пробелы'");
        }
    }

    @Test
    public void emptyNameUserTest(){
        User user = new User("testUser@yandex.ru",
                "User",
                "",
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Объект user создан не корректно");
        assertTrue(user.getName().equals(user.getLogin()),
                String.format("Имя пользователя - '%s', не совпадает с логином - '%s'", user.getName(), user.getLogin()));
    }

    @Test
    public void nullNameUserTest(){
        User user = new User("testUser@yandex.ru",
                "User",
                null,
                "2000-01-01");
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Объект user создан не корректно");
        assertTrue(user.getName().equals(user.getLogin()),
                String.format("Имя пользователя - '%s', не совпадает с логином - '%s'", user.getName(), user.getLogin()));
    }

    @Test
    public void birthdayIsAfterNowTest(){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String birthdayUser = LocalDate.now().plusDays(1).format(dateFormat);
        User user = new User("testUser@yandex.ru",
                "User",
                "Test",
                birthdayUser);
        user.setId(1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> userConstraintViolation : violations){
            assertTrue(userConstraintViolation.getMessageTemplate().equals("Дата рождения не может быть в будущем"),
                    "Отсутствует сообщение 'Дата рождения не может быть в будущем'");
        }
    }
}
