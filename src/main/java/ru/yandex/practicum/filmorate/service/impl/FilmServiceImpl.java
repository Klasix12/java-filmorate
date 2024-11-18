package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmRepository;
import ru.yandex.practicum.filmorate.storage.GenreRepository;
import ru.yandex.practicum.filmorate.storage.MpaRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.validation.Validation.isEmptyString;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Override
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        Collection<Film> films = filmRepository.findAll();
        for (Film film : films) {
            setMpaAndGenres(film);
        }
        return films;
    }

    @Override
    public Film findById(long id) {
        Film film = findFilmByIdOrThrow(id);
        setMpaAndGenres(film);
        log.info("Получение фильма с id {}", id);
        return film;
    }

    @Override
    public Film create(Film film) {
        try {
            Film savedFilm = filmRepository.create(film);
            if (film.getGenres() != null) {
                genreRepository.addFilmGenres(film.getId(), film.getGenres());
            }
            setMpaAndGenres(savedFilm);
            log.info("Добавление фильма {}", film);
            return savedFilm;
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при добавлении фильма {}", film);
            throw new ValidationException("Ошибка при добавлении фильма");
        }
    }


    @Override
    public Film update(Film film) {
        Film oldFilm = findFilmByIdOrThrow(film.getId());
        if (!isGenresEquals(oldFilm.getGenres(), film.getGenres())) {
            genreRepository.updateFilmGenres(film.getId(), film.getGenres());
        }
        oldFilm = updateFilmData(oldFilm, film);
        setMpaAndGenres(oldFilm);
        filmRepository.update(oldFilm);
        log.info("Обновление фильма {}", oldFilm);
        return oldFilm;
    }

    @Override
    public void addLike(long filmId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        findFilmByIdOrThrow(filmId);
        filmRepository.addLike(filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        findFilmByIdOrThrow(filmId);
        try {
            filmRepository.deleteLike(filmId, userId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
        log.info("Удален лайк пользователя с id {} у фильма с id {}", userId, filmId);
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        log.info("Получение популярных фильмов");
        return filmRepository.findPopularFilms(count);
    }

    private Film findFilmByIdOrThrow(long filmId) {
        log.error("Не удалось найти фильм с id {}", filmId);
        return filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден."));
    }

    private Film updateFilmData(Film oldFilm, Film newFilm) {
        return Film.builder()
                .id(oldFilm.getId())
                .name(isEmptyString(newFilm.getName()) ? oldFilm.getName() : newFilm.getName())
                .description(isEmptyString(newFilm.getDescription()) ? oldFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? oldFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? oldFilm.getReleaseDate() : newFilm.getReleaseDate())
                .mpa(newFilm.getMpa() == null ? oldFilm.getMpa() : newFilm.getMpa())
                .genres(newFilm.getGenres() == null || newFilm.getGenres().isEmpty() ? oldFilm.getGenres() : newFilm.getGenres())
                .build();
    }

    private void setMpaAndGenres(Film film) {
        film.setMpa(mpaRepository.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Не удалось найти рейтинг с id " + film.getMpa().getId())));
        film.setGenres(genreRepository.findGenresByFilmId(film.getId()).stream().toList());
    }

    private boolean isGenresEquals(List<Genre> oldGenres, List<Genre> newGenres) {
        if (oldGenres == null || newGenres == null) {
            return oldGenres == newGenres;
        }
        return oldGenres.size() == newGenres.size() && oldGenres.containsAll(newGenres);
    }
}
