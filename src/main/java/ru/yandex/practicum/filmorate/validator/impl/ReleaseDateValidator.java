package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValid, Film> {
    private static final String ERROR_RELEASE_DATE_IS_BEFORE_START_DATE = "Дата релиза — не раньше 28 декабря 1895";
    private static final LocalDate startDate = LocalDate.of(1895, 12, 27);
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean isValid(Film film, ConstraintValidatorContext context){
        if(film == null){
            return true;
        }
        if(LocalDate.parse(film.getReleaseDate(), dateFormat).isBefore(startDate)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ERROR_RELEASE_DATE_IS_BEFORE_START_DATE))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
