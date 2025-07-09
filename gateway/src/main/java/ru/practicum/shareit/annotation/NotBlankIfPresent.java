package ru.practicum.shareit.annotation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import ru.practicum.shareit.validator.NotBlankIfPresentValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NotBlankIfPresentValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface NotBlankIfPresent {
    String message() default "{jakarta.validation.constraints.NotBlank.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
