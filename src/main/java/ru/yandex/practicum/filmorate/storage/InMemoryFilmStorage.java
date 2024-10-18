package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.trace("Получение всех фильмов");
        return films.values();
    }

    @Override
    public Film findById(long id) {
        log.trace("Получение фильма по id");
        if (films.containsKey(id)) {
            log.info("Получение фильма с id {}", id);
            return films.get(id);
        }
        log.error("Фильм с id {} не найден", id);
        throw new NotFoundException("Фильм с id " + id + " не найден.");
    }

    @Override
    public Film create(Film film) {
        log.trace("Создание фильма");
        if (Film.isCorrectReleaseDate(film.getReleaseDate())) {
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Добавлен фильм: {}", film);
            return film;
        }
        log.error("Некорректная дата выпуска фильма");
        throw new ValidationException("Фильм не может быть выпущен раньше 28 декабря 1895");
    }

    @Override
    public Film update(Film newFilm) {
        log.trace("Обновление фильма");
        if (!Film.isCorrectReleaseDate(newFilm.getReleaseDate())) {
            log.error("Некорректная дата выпуска фильма");
            throw new ValidationException("Фильм не может быть выпушен раньше 28 декабря 1895");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm = updateFilmData(oldFilm, newFilm);
            films.put(oldFilm.getId(), oldFilm);
            log.info("Обновление фильма: {}", oldFilm);
            return oldFilm;
        }
        log.error("Id {} обновляемого фильма не найдено", newFilm.getId());
        throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
    }

    private Film updateFilmData(Film oldFilm, Film newFilm) {
        return Film.builder()
                .id(oldFilm.getId())
                .name(isEmptyString(newFilm.getName()) ? oldFilm.getName() : newFilm.getName())
                .description(isEmptyString(newFilm.getDescription()) ? oldFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? oldFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? oldFilm.getReleaseDate() : newFilm.getReleaseDate())
                .build();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
