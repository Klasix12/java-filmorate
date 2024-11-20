package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Collection<Genre> findAll() {
        log.trace("Получение всех жанров");
        return genreRepository.findAll();
    }

    @Override
    public Genre findById(long id) {
        log.info("Получение жанра с id {}", id);
        return genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не удалось найти жанр с id " + id));
    }
}
