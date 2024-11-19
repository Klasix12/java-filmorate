package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserRepository;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> findAll() {
        log.trace("Получение всех пользователей");
        return userRepository.findAll();
    }

    @Override
    public User create(User user) {
        if (Validation.isEmptyString(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("Создание пользователя {}", user);
        return userRepository.create(user);
    }

    @Override
    public User update(User user) {
        User oldUser = findUserByIdOrThrow(user.getId());
        oldUser = updateUserData(oldUser, user);
        userRepository.update(oldUser);
        log.info("Обновление пользователя {}", oldUser);
        return oldUser;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        try {
            userRepository.addFriend(userId, friendId);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при добавлении в друзья");
            throw new NotFoundException("Не удалось добавить пользователя в друзья");
        }
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        findUserByIdOrThrow(userId);
        findUserByIdOrThrow(friendId);
        userRepository.deleteFriend(userId, friendId);
        log.info("Пользователи с id {} и {} удалили друг-друга из друзей", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        log.info("Пользователь с id {} получает список своих друзей", userId);
        findUserByIdOrThrow(userId);
        return userRepository.findUserFriends(userId);
    }

    @Override
    public Collection<User> getMutualFriends(long userId, long otherUserId) {
        log.info("Пользователь с id {} получил список общих друзей пользователя с id {}", userId, otherUserId);
        return userRepository.findMutualFriends(userId, otherUserId);
    }

    private User findUserByIdOrThrow(long userId) {
        log.error("Не удалось найти пользователя с id {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private User updateUserData(User oldUser, User newUser) {
        return User.builder()
                .id(oldUser.getId())
                .email(isEmptyString(newUser.getEmail()) ? oldUser.getEmail() : newUser.getEmail())
                .login(isEmptyString(newUser.getLogin()) ? oldUser.getLogin() : newUser.getLogin())
                .name(isEmptyString(newUser.getName()) ? oldUser.getName() : newUser.getName())
                .birthday(newUser.getBirthday() == null ? oldUser.getBirthday() : newUser.getBirthday())
                .build();
    }
}
