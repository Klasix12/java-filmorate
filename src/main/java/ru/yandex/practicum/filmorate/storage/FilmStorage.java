package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> findById(long id);

    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> getPopularFilms(int count);
}
