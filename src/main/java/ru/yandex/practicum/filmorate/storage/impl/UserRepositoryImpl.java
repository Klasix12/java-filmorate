package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;

@Repository
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String CREATE_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String INSERT_FRIENDS_QUERY = "INSERT INTO friends (user_1_id, user_2_id, confirmed) VALUES (?, ?, ?)";
    private static final String FIND_FRIENDS_BY_ID_QUERY = "SELECT u.* FROM USERS u JOIN FRIENDS f ON u.id = f.user_2_id WHERE f.user_1_id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user_1_id = ? AND user_2_id = ?";
    private static final String FIND_MUTUAL_FRIENDS_QUERY = "SELECT * FROM users WHERE id IN (SELECT user_2_id FROM friends " +
            " WHERE user_1_id = ? AND user_2_id IN (SELECT user_2_id FROM friends WHERE user_1_id = ?))";

    public UserRepositoryImpl(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public User create(User user) {
        user.setId(insert(
                CREATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(Instant.from(user.getBirthday().atStartOfDay().toInstant(ZoneOffset.UTC)))));
        return user;
    }

    @Override
    public int update(User user) {
        return update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(Instant.from(user.getBirthday().atStartOfDay().toInstant(ZoneOffset.UTC))), user.getId());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        boolean confirmed = update("UPDATE friends SET confirmed = true WHERE user_1_id = ? AND user_2_id = ?", friendId, userId) > 0;
        update(INSERT_FRIENDS_QUERY, userId, friendId, confirmed);
    }

    @Override
    public Collection<User> findUserFriends(long userId) {
        return findMany(FIND_FRIENDS_BY_ID_QUERY, userId);
    }

    @Override
    public int deleteFriend(long userId, long friendId) {
        update("UPDATE friends SET confirmed = false WHERE user_1_id = ? AND user_2_id = ?", friendId, userId);
        return update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public Collection<User> findMutualFriends(long userId, long otherUserId) {
        return findMany(FIND_MUTUAL_FRIENDS_QUERY, userId, otherUserId);
    }
}
