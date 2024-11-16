package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaRepository;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public Collection<Mpa> findAll() {
        return mpaRepository.findAll();
    }

    @Override
    public Mpa findById(long id) {
        return mpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не удалось найти рейтинг с id " + id));
    }
}
