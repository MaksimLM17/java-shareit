package ru.practicum.shareit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.annotation.NotBlankIfPresent;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Разрешаем null, проверяем не-null значения
        return value == null || !value.trim().isEmpty();
    }
}
