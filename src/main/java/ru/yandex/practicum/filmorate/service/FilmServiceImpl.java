package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(long filmId, long userId) {
        log.trace("Добавление лайка");
        if (userStorage.findById(userId) != null) {
            log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
            filmStorage.findById(filmId).getLikes().add(userId);
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.trace("Удаление лайка");
        Film film = filmStorage.findById(filmId);
        if (film.getLikes().contains(userId)) {
            log.info("Удален лайк пользователя с id {} у фильма с id {}", userId, filmId);
            film.getLikes().remove(userId);
        } else {
            log.error("Пользователь с id {} не ставил лайк фильму с id {}", userId, filmId);
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        log.trace("Получение популярных фильмов");
        Collection<Film> films = filmStorage.findAll();
        return films.stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .collect(Collectors.toList())
                .reversed()
                .subList(0, Integer.min(count, films.size()));
    }
}
