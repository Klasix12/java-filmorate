package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Month;

@Data
@Builder
public class Film {

    private Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public static boolean isCorrectReleaseDate(LocalDate releaseDate) {
        return releaseDate.isAfter(MIN_RELEASE_DATE);
    }
}
