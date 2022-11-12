package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private Long duration;
    private static int idCounter = 1;

    public Film(String name, String description, String releaseDate, Long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = idCounter++;
    }
}
