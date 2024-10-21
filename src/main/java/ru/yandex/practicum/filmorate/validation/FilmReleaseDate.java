package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilmReleaseDateValidation.class)
@Documented
public @interface FilmReleaseDate {
    String message() default "Фильм не может быть выпушен раньше 28 декабря 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
