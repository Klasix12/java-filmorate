package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.impl.GenreRepositoryImpl;

@JdbcTest
@Import({GenreRepositoryImpl.class, GenreRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreRepositoryImplTests {
    private final GenreRepositoryImpl genreRepository;
    private static final int genreCount = 6;

    @Test
    public void testFindAll() {
        Assertions.assertEquals(genreCount, genreRepository.findAll().size());
    }

    @Test
    public void testFindById() {
        Assertions.assertEquals("Комедия", genreRepository.findById(1).get().getName());
    }
}
