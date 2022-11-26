package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.impl.BirthdayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthdayValidator.class)
public @interface BirthdayValid {
    String message() default "Не корректная дата рождения";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
