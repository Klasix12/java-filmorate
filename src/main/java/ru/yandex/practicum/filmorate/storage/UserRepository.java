package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAll();

    Optional<User> findById(long id);

    User create(User user);

    int update(User user);

    void addFriend(long userId, long friendId);

    Collection<User> findUserFriends(long userId);

    int deleteFriend(long userId, long friendId);

    Collection<User> findMutualFriends(long userId, long otherUserId);
}
