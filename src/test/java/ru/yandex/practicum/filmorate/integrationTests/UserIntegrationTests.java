package ru.yandex.practicum.filmorate.integrationTests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserIntegrationTests {

    final UserDbStorage userStorage;

    @BeforeEach
    void createdUserForDB(){
        if(userStorage.findAll().size() != 2){
            User firstTestUser = new User("testUserOne@yandex.ru",
                    "UserOne",
                    "Tester",
                    "2000-01-01");
            userStorage.addUser(firstTestUser);
            User SecondTestUser = new User("testUserTwo@yandex.ru",
                    "UserTwo",
                    "Toster",
                    "2000-02-01");
            userStorage.addUser(SecondTestUser);
        }
        userStorage.deleteFriends(1, 2);
    }

    @Test
    public void testCreatedUser() {
        checkFindUserById(1);
        checkFindUserById(2);
    }

    @Test
    public void testFindAll() {
        Collection<User> currentList = userStorage.findAll();
        assertTrue(currentList.size() == 2, "Не корректное количество пользователей");
    }

    @Test
    public void testUpgradeUser() {
        User updateUser = new User("updateUser@yandex.ru",
                "updateUser",
                "UpdateName",
                "2000-10-10");
        updateUser.setId(1);
        userStorage.upgradeUser(updateUser);

        Optional<User> userOptional = userStorage.findUser(1);

        Map <String, Object>  mapForCheck = new HashMap<>();
        mapForCheck.put("id", updateUser.getId());
        mapForCheck.put("email", updateUser.getEmail());
        mapForCheck.put("login", updateUser.getLogin());
        mapForCheck.put("name", updateUser.getName());
        mapForCheck.put("birthday", updateUser.getBirthday());
        for (Map.Entry<String, Object> entry : mapForCheck.entrySet()) {
            assertThat(userOptional)
                    .isPresent()
                    .hasValueSatisfying(user ->
                            assertThat(user).hasFieldOrPropertyWithValue(entry.getKey(), entry.getValue())
                    );
        }
    }

    @Test
    public void testFindUserById() {
        checkFindUserById(1);
    }

    @Test
    public void testAddRequestsFriendship() {
        assertTrue(userStorage.addRequestsFriendship(1, 2), "Запрос на дружбу не отправлен");
        assertFalse(userStorage.addRequestsFriendship(1, 2), "Запрос на дружбу не должен быть отправлен");
    }

    @Test
    public void testDeleteFriends() {
        userStorage.addRequestsFriendship(1, 2);
        assertTrue(userStorage.deleteFriends(1, 2), "Запрос на дружбу не удален");
        assertFalse(userStorage.deleteFriends(1,2), "Запрос на дружбу не должен быть удален");
    }

    @Test
    public void testFindAllFriends(){
        userStorage.addRequestsFriendship(1, 2);
        List<Integer> listFriendIdOne = userStorage.findAllFriends(1);
        assertTrue(listFriendIdOne.size() == 1, "В списке друзей должен быть 1 друг");
        assertTrue(listFriendIdOne.get(0) == 2, "Значение ID друга должно равнятся 2");

        List<Integer> listFriendIdTwo = userStorage.findAllFriends(2);
        assertTrue(listFriendIdTwo.size() == 0, "В списке друзей НЕ должено быть друзей");

    }

    void checkFindUserById(Integer userId){
        Optional<User> userOptional = userStorage.findUser(userId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userId)
                );
    }
}
