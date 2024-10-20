package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException("Пользователь с id " + id + " не найден.");
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser = updateUserData(oldUser, newUser);
            users.put(oldUser.getId(), oldUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
    }

    private User updateUserData(User oldUser, User newUser) {
        return User.builder()
                .id(oldUser.getId())
                .email(isEmptyString(newUser.getEmail()) ? oldUser.getEmail() : newUser.getEmail())
                .login(isEmptyString(newUser.getLogin()) ? oldUser.getLogin() : newUser.getLogin())
                .name(isEmptyString(newUser.getName()) ? oldUser.getName() : newUser.getName())
                .birthday(newUser.getBirthday() == null ? oldUser.getBirthday() : newUser.getBirthday())
                .friends(newUser.getFriends() == null ? oldUser.getFriends() : newUser.getFriends())
                .build();
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
