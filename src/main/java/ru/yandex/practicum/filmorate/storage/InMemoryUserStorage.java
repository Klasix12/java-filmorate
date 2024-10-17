package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.trace("Получение всех пользователей");
        return users.values();
    }

    @Override
    public User create(User user) {
        log.trace("Создание пользователя");
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
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
