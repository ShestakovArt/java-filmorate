package ru.yandex.practicum.filmorate.validateTests;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmTests {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    String description = "Немецкий кинофильм 1997 года режиссёра Томаса Яна о двух мужчинах, " +
            "которым ставят смертельный диагноз, " +
            "в результате чего они угоняют машину с миллионом немецких марок в багажнике и покидают больницу.";

    @Test
    public void correctlyCreatedFilmTest() {
        Film film = new Film("Достучатся до небес",
                description,
                "1997-02-20",
                87,
                null,
                null,
                null,
                null
        );
        film.setId(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Объект film создан не корректно");
    }

    @Test
    public void emptyNameFilmTest() {
        Film film = new Film("",
                description,
                "1997-02-20",
                87, null, null, null, null);
        film.setId(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> filmConstraintViolation : violations) {
            assertTrue(filmConstraintViolation.getMessageTemplate().equals("Название не может быть пустым"),
                    "Отсутствует сообщение 'Название не может быть пустым'");
        }
    }

    @Test
    public void sizeDescriptionFilmMore200SymbolTest() {
        Film film = new Film("Достучатся до небес",
                description + "Увеличим описание до значения более 200 символов",
                "1997-02-20",
                87, null, null, null, null);
        film.setId(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> filmConstraintViolation : violations) {
            assertTrue(filmConstraintViolation.getMessageTemplate().equals("Максимальная длина описания — 200 символов"),
                    "Отсутствует сообщение 'Максимальная длина описания — 200 символов'");
        }
    }

    @Test
    public void releaseDateIsBeforeDateOfReleaseDateFirstFilmToWorldTest() {
        Film film = new Film("Достучатся до небес",
                description,
                "1895-12-26",
                87, null, null, null, null);
        film.setId(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> filmConstraintViolation : violations) {
            assertTrue(filmConstraintViolation.getMessageTemplate().equals("Дата релиза — не раньше 28 декабря 1895"),
                    "Отсутствует сообщение 'Дата релиза — не раньше 28 декабря 1895'");
        }
    }

    @Test
    public void durationIsNegativeTest() {
        Film film = new Film("Достучатся до небес",
                description,
                "1997-02-20",
                -1, null, null, null, null);
        film.setId(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> filmConstraintViolation : violations) {
            assertTrue(filmConstraintViolation.getMessageTemplate().equals("Продолжительность фильма должна быть положительной"),
                    "Отсутствует сообщение 'Продолжительность фильма должна быть положительной'");
        }
    }
}
