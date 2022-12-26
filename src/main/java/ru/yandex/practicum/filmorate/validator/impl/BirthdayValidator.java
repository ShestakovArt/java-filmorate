package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.BirthdayValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BirthdayValidator implements ConstraintValidator<BirthdayValid, User> {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String ERROR_BIRTHDAY = "Дата рождения не может быть в будущем";
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context){
        if(user == null){
            return true;
        }
        if(LocalDate.parse(user.getBirthday(), dateFormat).isAfter(LocalDate.now())
                || LocalDate.parse(user.getBirthday(), dateFormat).isEqual(LocalDate.now())){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ERROR_BIRTHDAY))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
