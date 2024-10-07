package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Long id;

    @Email
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^\\S*$") // строка, состоящая только из непробельных символов
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
