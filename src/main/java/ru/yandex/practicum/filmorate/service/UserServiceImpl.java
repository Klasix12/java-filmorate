package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User create(User user) {
        user.setFriends(new HashSet<>());
        if (Validation.isEmptyString(user.getName())) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        User oldUser = findUserByIdOrThrow(user.getId());
        oldUser = updateUserData(oldUser, user);
        return userStorage.update(oldUser);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = findUserByIdOrThrow(userId);
        User friendUser = findUserByIdOrThrow(friendId);
        user.getFriends().add(friendUser.getId());
        friendUser.getFriends().add(user.getId());
        log.info("Пользователи с id {} и {} добавили друг-друга в друзья", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = findUserByIdOrThrow(userId);
        User friendUser = findUserByIdOrThrow(friendId);
        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(userId);
        log.info("Пользователи с id {} и {} удалили друг-друга из друзей", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        log.info("Пользователь с id {} получает список своих друзей", userId);
        return findUserByIdOrThrow(userId).getFriends().stream()
                .map(this::findUserByIdOrThrow)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getMutualFriends(long userId, long otherUserId) {
        User user = findUserByIdOrThrow(userId);
        User otherUser = findUserByIdOrThrow(otherUserId);
        log.info("Пользователь с id {} получил список общих друзей пользователя с id {}", userId, otherUserId);
        return user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .map(this::findUserByIdOrThrow)
                .collect(Collectors.toList());
    }

    private User findUserByIdOrThrow(long userId) {
        log.error("Не удалось найти пользователя с id {}", userId);
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
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
}
