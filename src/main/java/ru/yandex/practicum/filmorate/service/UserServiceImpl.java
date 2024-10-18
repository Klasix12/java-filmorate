package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public void addFriend(long userId, long friendId) {
        log.trace("Добавление друга");
        User user = userStorage.findById(userId);
        User friendUser = userStorage.findById(friendId);
        user.getFriends().add(friendUser.getId());
        friendUser.getFriends().add(user.getId());
        log.info("Пользователи с id {} и {} добавили друг-друга в друзья", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        log.trace("Удаление друга");
        User user = userStorage.findById(userId);
        User friendUser = userStorage.findById(friendId);
        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(userId);
        log.info("Пользователи с id {} и {} удалили друг-друга из друзей", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        log.info("Пользователь с id {} получает список своих друзей", userId);
        return userStorage.findById(userId).getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getMutualFriends(long userId, long otherUserId) {
        log.trace("Получение общих друзей");
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherUserId);
        log.info("Пользователь с id {} получил список общих друзей пользователя с id {}", userId, otherUserId);
        return user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
