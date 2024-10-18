package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    //    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    //    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //    Если значение параметра count не задано, верните первые 10
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(long filmId, long userId) {
        if (userStorage.findById(userId) != null) {
            filmStorage.findById(filmId).getLikes().add(userId);
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .collect(Collectors.toList())
                .subList(0, count);
    }
}
