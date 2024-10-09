package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.groups.OnCreate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @NotNull(groups = OnUpdate.class)
    private Long id;

    @Email(message = "Неверный формат email", groups = OnCreate.class)
    private String email;

    @Pattern(message = "Неверный формат логина", regexp = "^\\S*$", groups = {OnCreate.class, OnUpdate.class}) // строка, состоящая только из непробельных символов
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;
}
