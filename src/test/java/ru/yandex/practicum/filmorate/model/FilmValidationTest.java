package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validation.groups.OnCreate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullId() {
        Film film = Film.builder()
                .id(null)
                .name("qwerty")
                .description("qwerty")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnUpdate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    public void testBlankName() {
        Film film = Film.builder()
                .id(123L)
                .name(" ")
                .description("qwerty")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name") &&
                        v.getMessage().equals("Имя не должно быть пустым")
                ));
    }

    @Test
    public void testSizeDescription() {
        Film film = Film.builder()
                .id(123L)
                .name("qwerty")
                .description("q".repeat(201))
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description") &&
                        v.getMessage().equals("Максимальная длина описания фильма 200 символов")
                ));
    }

    @Test
    public void testReleaseDate() {
        Film film = Film.builder()
                .id(123L)
                .name("qwerty")
                .description("qwerty")
                .releaseDate(LocalDate.of(1800, 10, 10))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate") &&
                        v.getMessage().equals("Фильм не может быть выпушен раньше 28 декабря 1895")
                ));
    }

    @Test
    public void testNegativeDuration() {
        Film film = Film.builder()
                .id(123L)
                .name("qwerty")
                .description("qwerty")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(-1)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration") &&
                        v.getMessage().equals("Продолжительность должна быть положительным числом")
                ));
    }
}
