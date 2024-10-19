package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.groups.OnCreate;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotEmpty(groups = OnCreate.class)
    @Email(message = "Неверный формат email", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    // строка, состоящая только из непробельных символов
    @Pattern(message = "Неверный формат логина", regexp = "^\\S*$", groups = {OnCreate.class, OnUpdate.class})
    private String login;

    private String name;

    @JsonIgnore
    private Set<Long> friends;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;
}
