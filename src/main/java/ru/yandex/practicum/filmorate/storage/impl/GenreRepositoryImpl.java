package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepositoryImpl extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = "SELECT g.* FROM genres AS g JOIN film_genres AS fg ON fg.film_id = ? WHERE fg.genre_id = id;";
    private static final String CREATE_FILM_GENRES_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_BY_FILM_ID_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    public GenreRepositoryImpl(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Genre> findGenresByFilmId(long id) {
        return findMany(FIND_ALL_BY_FILM_ID_QUERY, id);
    }

    @Override
    public void addFilmGenres(long filmId, Collection<Genre> genres) {
        batchInsert(CREATE_FILM_GENRES_QUERY, createBatchParams(filmId, genres));
    }

    @Override
    public void updateFilmGenres(long filmId, List<Genre> genres) {
        update(DELETE_GENRES_BY_FILM_ID_QUERY, filmId);
        batchInsert(CREATE_FILM_GENRES_QUERY, createBatchParams(filmId, genres));
    }

    private List<Object[]> createBatchParams(long filmId, Collection<Genre> genres) {
        return genres.stream()
                .distinct()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .toList();
    }
}
