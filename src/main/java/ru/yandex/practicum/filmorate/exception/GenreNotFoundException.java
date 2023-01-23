package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends RuntimeException {

    private static final String ERROR_MES = "Genre with id=%s not found.";

    public GenreNotFoundException(Integer genreId) {
        super(String.format(ERROR_MES, genreId));
    }
}
