package ru.yandex.practicum.filmorate.integrationTests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level= AccessLevel.PRIVATE)
public class MpaIntegrationTests {
    final MpaDbStorage mpaDbStorage;

    @Test
    public void testFindNameMpa() {
        LinkedList<String> nameMpa = new LinkedList<>();
        nameMpa.add("G");
        nameMpa.add("PG");
        nameMpa.add("PG-13");
        nameMpa.add("R");
        nameMpa.add("NC-17");
        for (int i = 0; i < nameMpa.size(); i++){
            assertTrue(mpaDbStorage.findNameMpa(i+1).equals(nameMpa.get(i)), "Не корректное название рейтинга");
        }
    }

    @Test
    public void testFindAll() {
        assertTrue(mpaDbStorage.findAll().size() == 5, "Размер коллекции рейтингов MPA не соответсвует");
    }
}
