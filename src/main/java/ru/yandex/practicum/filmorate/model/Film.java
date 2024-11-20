package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;
import ru.yandex.practicum.filmorate.validation.groups.OnCreate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(exclude = {"mpa", "genres"})
public class Film {
    @NotNull(message = "", groups = {OnUpdate.class})
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = {OnCreate.class})
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма 200 символов", groups = {OnCreate.class})
    private String description;

    @FilmReleaseDate(message = "Фильм не может быть выпушен раньше 28 декабря 1895", groups = {OnCreate.class})
    private LocalDate releaseDate;

    @PositiveOrZero(message = "Продолжительность должна быть положительным числом", groups = {OnCreate.class})
    private Integer duration;

    private Mpa mpa;

    private List<Genre> genres;
}
