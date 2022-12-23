package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
//	private final UserDbStorage userStorage;
//
//	@Test
//	public void testFindUserById() {
//		User userForTest = new User("email@string.true","loginTest", "nameTest", "2000-12-22");
//		userStorage.addUser(userForTest);
//		Optional<User> userOptional = userStorage.findUser(1);
//
//		assertThat(userOptional)
//				.isPresent()
//				.hasValueSatisfying(user ->
//						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
//				);
//	}

}
