package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Collection<Film> findAll();

    Optional<Film> findById(long id);

    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> getPopularFilms(int count);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);
}
