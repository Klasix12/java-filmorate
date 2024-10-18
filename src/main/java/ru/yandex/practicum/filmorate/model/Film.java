package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.groups.OnCreate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

@Data
@Builder
public class Film {

    @NotNull(message = "", groups = {OnUpdate.class})
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = {OnCreate.class})
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма 200 символов", groups = {OnCreate.class})
    private String description;

    private LocalDate releaseDate;

    @PositiveOrZero(message = "Продолжительность должна быть положительным числом", groups = {OnCreate.class})
    private Integer duration;

    private Set<Long> likes;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public static boolean isCorrectReleaseDate(LocalDate releaseDate) {
        return releaseDate.isAfter(MIN_RELEASE_DATE) || releaseDate.equals(MIN_RELEASE_DATE);
    }
}
