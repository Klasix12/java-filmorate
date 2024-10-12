package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.groups.OnCreate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.trace("Получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Validated(OnCreate.class) @RequestBody User user) {
        log.trace("Создание пользователя");
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User newUser) {
        log.trace("Обновление пользователя");
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser = User.builder()
                    .id(oldUser.getId())
                    .email(isEmptyString(newUser.getEmail()) ? oldUser.getEmail() : newUser.getEmail())
                    .login(isEmptyString(newUser.getLogin()) ? oldUser.getLogin() : newUser.getLogin())
                    .name(isEmptyString(newUser.getName()) ? oldUser.getName() : newUser.getName())
                    .birthday(newUser.getBirthday() == null ? oldUser.getBirthday() : newUser.getBirthday())
                    .build();
            users.put(oldUser.getId(), oldUser);
            log.info("Обновление пользователя: {}", oldUser);
            return oldUser;
        }
        log.error("Id {} обновляемого пользователя не найдено", newUser.getId());
        throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
