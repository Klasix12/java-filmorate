package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (Film.isCorrectReleaseDate(film.getReleaseDate())) {
            film.setId(getNextId());
            films.put(film.getId(), film);
            return film;
        }
        throw new ValidationException("Фильм не может быть выпушен раньше 28 декабря 1895");
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!Film.isCorrectReleaseDate(newFilm.getReleaseDate())) {
            throw new ValidationException("Фильм не может быть выпушен раньше 28 декабря 1895");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm = Film.builder()
                    .id(oldFilm.getId())
                    .name(newFilm.getName())
                    .description(newFilm.getDescription())
                    .duration(newFilm.getDuration())
                    .releaseDate(newFilm.getReleaseDate())
                    .build();
            films.put(oldFilm.getId(), oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");

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
