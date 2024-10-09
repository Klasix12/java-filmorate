package ru.yandex.practicum.filmorate.validation;

public class Validation {
    private Validation() {}

    public static boolean isEmptyString(String str) {
        return str == null || str.isBlank() || str.isEmpty();
    }
}
