package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    void addFriend(long userId, long friendId);
    void deleteFriend(long userId, long friendId);
    Collection<User> getFriends(long userId);
    Collection<User> getMutualFriends(long userId, long otherUserId);
}
