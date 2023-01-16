package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class SearchParamsValidator implements ConstraintValidator<ReleaseDateValid, Set<String>> {

    private static final Set<String> validSet = Set.of("director", "title");
    private static final String ERROR_MES = "Search params must be 'director' and/or 'title'";

    @Override
    public void initialize(ReleaseDateValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Set<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        if (validSet.containsAll(strings)) {
            return true;
        } else {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(ERROR_MES)
                    .addConstraintViolation();
            return false;
        }
    }
}
