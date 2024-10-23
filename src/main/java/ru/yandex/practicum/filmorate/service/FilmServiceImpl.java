package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(Film film) {
        film.setLikes(new HashSet<>());
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        Film oldFilm = findFilmByIdOrThrow(film.getId());
        oldFilm = updateFilmData(oldFilm, film);
        return filmStorage.update(oldFilm);
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (userStorage.findById(userId).isPresent()) {
            findFilmByIdOrThrow(filmId).getLikes().add(userId);
            log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
            return;
        }
        throw new NotFoundException("Пользователь с id " + userId + " не найден");
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = findFilmByIdOrThrow(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
        film.getLikes().remove(userId);
        log.info("Удален лайк пользователя с id {} у фильма с id {}", userId, filmId);
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private Film findFilmByIdOrThrow(long filmId) {
        log.error("Не удалось найти фильм с id {}", filmId);
        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден."));
    }

    private Film updateFilmData(Film oldFilm, Film newFilm) {
        return Film.builder()
                .id(oldFilm.getId())
                .name(isEmptyString(newFilm.getName()) ? oldFilm.getName() : newFilm.getName())
                .description(isEmptyString(newFilm.getDescription()) ? oldFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? oldFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? oldFilm.getReleaseDate() : newFilm.getReleaseDate())
                .likes(newFilm.getLikes() == null ? oldFilm.getLikes() : newFilm.getLikes())
                .build();
    }
}
