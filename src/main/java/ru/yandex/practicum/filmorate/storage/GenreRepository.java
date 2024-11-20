package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    Collection<Genre> findAll();

    Optional<Genre> findById(long id);

    Collection<Genre> findGenresByFilmId(long id);

    void addFilmGenres(long filmId, Collection<Genre> genres);

    void updateFilmGenres(long filmId, List<Genre> genres);
}
