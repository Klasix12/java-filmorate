package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new NotFoundException("Фильм с id " + id + " не найден.");
    }

    @Override
    public Film create(Film film) {
        if (Film.isCorrectReleaseDate(film.getReleaseDate())) {
            film.setId(getNextId());
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            return film;
        }
        throw new ValidationException("Фильм не может быть выпущен раньше 28 декабря 1895");
    }

    @Override
    public Film update(Film newFilm) {
        if (!Film.isCorrectReleaseDate(newFilm.getReleaseDate())) {
            throw new ValidationException("Фильм не может быть выпушен раньше 28 декабря 1895");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm = updateFilmData(oldFilm, newFilm);
            films.put(oldFilm.getId(), oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
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

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
