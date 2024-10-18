package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public void addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friendUser = userStorage.findById(friendId);
        user.getFriends().add(friendUser.getId());
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        if (!user.getFriends().contains(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не является другом пользователя с id " + userId);
        }
        user.getFriends().remove(friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return userStorage.findById(userId).getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getMutualFriends(long userId, long otherUserId) {
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherUserId);
        return user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
