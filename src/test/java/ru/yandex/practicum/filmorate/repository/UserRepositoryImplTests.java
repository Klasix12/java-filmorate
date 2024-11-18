package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.List;

@JdbcTest
@Import({UserRepositoryImpl.class, UserRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryImplTests {
    private final UserRepositoryImpl userRepository;

    @Test
    public void testFindAll() {
        int userCount = 3;
        for (int i = 0; i < userCount; i++) {
            userRepository.create(createTestUser());
        }
        Assertions.assertEquals(userCount, userRepository.findAll().size());
    }

    @Test
    public void testFindById() {
        User user = userRepository.create(createTestUser());

        Assertions.assertEquals(user, userRepository.findById(user.getId()).get());
    }

    @Test
    public void testUpdate() {
        User user = userRepository.create(createTestUser());

        String newName = "test123";
        user.setName(newName);
        userRepository.update(user);

        Assertions.assertEquals(user, userRepository.findById(user.getId()).get());
    }

    @Test
    public void testAddFriendAndFindUserFriends() {
        User user1 = userRepository.create(createTestUser());
        User user2 = userRepository.create(createTestUser());
        userRepository.addFriend(user1.getId(), user2.getId());

        List<User> user1Friends = (List<User>) userRepository.findUserFriends(user1.getId());

        Assertions.assertEquals(user2, user1Friends.getFirst());
    }

    @Test
    public void testDeleteFriend() {
        User user1 = userRepository.create(createTestUser());
        User user2 = userRepository.create(createTestUser());
        userRepository.addFriend(user1.getId(), user2.getId());

        List<User> user1Friends = (List<User>) userRepository.findUserFriends(user1.getId());
        Assertions.assertEquals(user2, user1Friends.getFirst());

        userRepository.deleteFriend(user1.getId(), user2.getId());
        Assertions.assertEquals(0, userRepository.findUserFriends(user1.getId()).size());
    }

    @Test
    public void testFindMutualFriends() {
        User user1 = userRepository.create(createTestUser());
        User user2 = userRepository.create(createTestUser());
        User user3 = userRepository.create(createTestUser());
        userRepository.addFriend(user1.getId(), user3.getId());
        userRepository.addFriend(user2.getId(), user3.getId());

        List<User> mutualFriends = (List<User>) userRepository.findMutualFriends(user1.getId(), user2.getId());
        Assertions.assertEquals(user3, mutualFriends.getFirst());
    }

    private User createTestUser() {
        return User.builder()
                .email("test@test.com")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
    }
}
