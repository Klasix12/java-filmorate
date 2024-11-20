package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.FilmGenreRepository;

import java.util.List;
import java.util.Set;

@Repository
public class FilmGenreRepositoryImpl extends BaseRepository<FilmGenre> implements FilmGenreRepository {

    private final String findAllByFilmIdsQuery = "SELECT * FROM film_genres WHERE film_id IN";

    public FilmGenreRepositoryImpl(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<FilmGenre> findAllByFilmIds(Set<Long> ids) {
        String placeholders = String.join(", ", ids.stream().map(id -> "?").toArray(String[]::new));
        String query = findAllByFilmIdsQuery + "(" + placeholders + ")";

        return findMany(query, ids.toArray());
    }
}
