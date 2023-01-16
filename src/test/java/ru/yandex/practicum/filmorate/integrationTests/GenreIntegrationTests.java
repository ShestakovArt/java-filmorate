package ru.yandex.practicum.filmorate.integrationTests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreIntegrationTests {
    final GenreDbStorage genreDbStorage;

    @Test
    public void testFindNameGenre() {
        LinkedList<String> nameGenre = new LinkedList<>();
        nameGenre.add("Комедия");
        nameGenre.add("Драма");
        nameGenre.add("Мультфильм");
        nameGenre.add("Триллер");
        nameGenre.add("Документальный");
        nameGenre.add("Боевик");
        for (int i = 0; i < nameGenre.size(); i++) {
            assertTrue(genreDbStorage.findGenreById(i + 1).getName().equals(nameGenre.get(i)), "Не корректное название жанра");
        }
    }

    @Test
    public void testFindAll() {
        assertTrue(genreDbStorage.findAll().size() == 6, "Размер коллекции жанров не соответсвует");
    }
}
