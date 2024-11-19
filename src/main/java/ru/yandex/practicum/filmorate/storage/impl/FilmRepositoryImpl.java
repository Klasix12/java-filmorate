package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.FilmRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;

@Repository
public class FilmRepositoryImpl extends BaseRepository<Film> implements FilmRepository {
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.id WHERE f.id = ?";
    private static final String CREATE_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
    private static final String FIND_POPULAR_QUERY = "SELECT s.id, s.name, s.description, s.release_date, s.duration, s.mpa_id, s.mpa_name " +
            "FROM (SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, COUNT(l.user_id) AS user_likes " +
            "FROM films AS f " +
            "JOIN likes AS l ON f.id = l.film_id " +
            "JOIN mpa AS m ON f.mpa_id = m.id " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
            "ORDER BY user_likes DESC " +
            "LIMIT ?) AS s";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";


    public FilmRepositoryImpl(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Film create(Film film) {
        film.setId(insert(
                CREATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(Instant.from(film.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC))),
                film.getDuration(),
                film.getMpa().getId()));
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Timestamp.from(Instant.from(newFilm.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC))),
                newFilm.getDuration(),
                newFilm.getId());
        return newFilm;
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    @Override
    public void addLike(long filmId, long userId) {
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

}
