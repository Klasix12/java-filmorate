package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

public class FilmTest {
    @Test
    public void testFilmReleaseDateValidation() {
        Assertions.assertTrue(Film.isCorrectReleaseDate(LocalDate.of(1895, Month.DECEMBER, 28)));
        Assertions.assertTrue(Film.isCorrectReleaseDate(LocalDate.now()));
        Assertions.assertFalse(Film.isCorrectReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27)));
    }
}
