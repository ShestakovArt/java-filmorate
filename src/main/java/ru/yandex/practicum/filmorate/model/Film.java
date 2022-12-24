package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.*;

@Data
@ReleaseDateValid
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Film{

    int id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;
    String releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    Long duration;
    Integer rate;
    Mpa mpa;
    List<Genre> genres;

    Integer rateAndLikes;


    public Film(String name, String description, String releaseDate, int duration, Integer rate,
                Mpa mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Long.valueOf(duration);
        if(rate != null){
            this.rate = rate;
        } else {
            this.rate = 0;
        }
        this.mpa = mpa;
        if(genres == null){
            this.genres = new ArrayList<>();
        } else {
            this.genres = genres;
        }
    }

    public Map<String,Object> toMap() {
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
