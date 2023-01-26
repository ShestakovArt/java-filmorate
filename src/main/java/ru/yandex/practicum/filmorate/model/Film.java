package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ReleaseDateValid
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Film {

    Integer id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;
    String releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    Integer duration;
    Integer rate;
    Mpa mpa;
    List<Genre> genres;
    List<Director> directors;

    Integer rateAndLikes;


    public Film(String name, String description, String releaseDate, Integer duration, Integer rate,
                Mpa mpa, List<Genre> genres, List<Director> directors) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (rate != null) {
            this.rate = rate;
        } else {
            this.rate = 0;
        }
        this.mpa = mpa;
        if (genres == null) {
            this.genres = new ArrayList<>();
        } else {
            this.genres = genres;
        }
        this.directors = directors;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", name);
        values.put("FILM_DESCRIPTION", description);
        values.put("FILM_RELEASE_DATE", releaseDate);
        values.put("FILM_DURATION", duration);
        values.put("FILM_RATE", rate);
        values.put("MPA_ID", mpa.getId());
        values.put("FILM_RATE_AND_LIKES", rateAndLikes);

        return values;
    }
}
