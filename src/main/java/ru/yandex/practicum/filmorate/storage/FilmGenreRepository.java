package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;
import java.util.Set;

public interface FilmGenreRepository {
    List<FilmGenre> findAllByFilmIds(Set<Long> ids);
}
