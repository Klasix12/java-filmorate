package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmRepositoryImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmRepositoryImpl.class, FilmRowMapper.class,
        UserRepositoryImpl.class, UserRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryImplTests {
    private final FilmRepositoryImpl filmRepository;
    private final UserRepositoryImpl userRepository;
    private static long testUserId = 1;

    @Test
    public void testFindAll() {
        int size = 3;
        for (int i = 0; i < size; i++) {
            filmRepository.create(createTestFilm());
        }

        Assertions.assertEquals(size, filmRepository.findAll().size());
    }

    @Test
    public void testFindById() {
        Film film = filmRepository.create(createTestFilm());
        Assertions.assertEquals(film, filmRepository.findById(film.getId()).get());
    }

    @Test
    public void testAddFilm() {
        Film newFilm = filmRepository.create(createTestFilm());

        Assertions.assertTrue(filmRepository.findAll().size() == 1);
        Optional<Film> filmOptional = filmRepository.findById(newFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmOptional.get().getId()));
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = filmRepository.create(createTestFilm());
        Optional<Film> filmOptional = filmRepository.findById(newFilm.getId());

        Assertions.assertTrue(filmOptional.isPresent());

        filmOptional.get().setName("updateTest");
        filmRepository.update(filmOptional.get());

        filmOptional = filmRepository.findById(filmOptional.get().getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "updateTest"));
    }

    @Test
    public void testFindPopularFilms() {
        Film newFilm = filmRepository.create(createTestFilm());
        Film newFilm1 = filmRepository.create(createTestFilm());

        User user = userRepository.create(createTestUser());
        User user1 = userRepository.create(createTestUser());

        filmRepository.addLike(newFilm1.getId(), user.getId());
        filmRepository.addLike(newFilm1.getId(), user1.getId());
        filmRepository.addLike(newFilm.getId(), user.getId());

        List<Film> popularFilms = (List<Film>) filmRepository.findPopularFilms(2);
        Assertions.assertEquals(newFilm1, popularFilms.getFirst());
        Assertions.assertEquals(newFilm, popularFilms.get(1));
    }

    private Film createTestFilm() {
        return Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .duration(100)
                .mpa(Mpa.builder().id(1).build())
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .email("test" + testUserId++ + "@test.com")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
    }
}
