package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.impl.MpaRepositoryImpl;

@JdbcTest
@Import({MpaRepositoryImpl.class, MpaRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRepositoryImplTests {
    private final MpaRepositoryImpl mpaRepository;
    private final int MPA_COUNT = 5;

    @Test
    public void testFindAll() {
        Assertions.assertEquals(MPA_COUNT, mpaRepository.findAll().size());
    }

    @Test
    public void testFindById() {
        Assertions.assertEquals("G", mpaRepository.findById(1).get().getName());
    }
}
