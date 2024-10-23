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

public class UserValidationTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullId() {
        User user = User.builder()
                .id(null)
                .email("qwerty@qwerty.com")
                .login("qwerty")
                .name("qwerty")
                .birthday(LocalDate.of(2010, 10, 10))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user, OnUpdate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    public void testIncorrectEmail() {
        User user = User.builder()
                .id(123L)
                .email("qwerty.com@")
                .login("qwerty")
                .name("qwerty")
                .birthday(LocalDate.of(2010, 10, 10))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().equals("Неверный формат email")));
    }

    @Test
    public void testIncorrectLogin() {
        User user = User.builder()
                .id(123L)
                .email("qwerty@qwerty.com")
                .login("qw erty")
                .name("qwerty")
                .birthday(LocalDate.of(2010, 10, 10))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login") &&
                        v.getMessage().equals("Неверный формат логина")));
    }

    @Test
    public void testIncorrectBirthday() {
        User user = User.builder()
                .id(123L)
                .email("qwerty@qwerty.com")
                .login("qwerty")
                .name("qwerty")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday") &&
                        v.getMessage().equals("Дата рождения не может быть в будущем")));
    }

    @Test
    public void testTodayBirthday() {
        User user = User.builder()
                .id(123L)
                .email("qwerty@qwerty.com")
                .login("qwerty")
                .name("qwerty")
                .birthday(LocalDate.now())
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);
        assertTrue(violations.isEmpty());
    }
}
