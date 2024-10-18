package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    Collection<Film> findPopularFilms(int count);
}
