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

    @Positive
    Integer mpa;
    HashMap<String, Integer> mpaMap;
    List<HashMap<String, Integer>>genres;

    Integer rate;
    Set<Integer> likes = new HashSet<>();

    public Film(String name, String description, String releaseDate, int duration, Integer rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Long.valueOf(duration);
        if(rate == null || rate < 0){
            this.rate = likes.size();
        }else{
            this.rate = rate + likes.size();
        }
    }

    public Film(String name, String description, String releaseDate, int duration, Integer rate,
                HashMap<String, Integer> mpaMap) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Long.valueOf(duration);
        if(rate == null || rate < 0){
            this.rate = likes.size();
        }else{
            this.rate = rate + likes.size();
        }
        this.mpaMap = mpaMap;
        if(mpaMap.size() > 0){
            mpa = mpaMap.get("id");
        }
    }

    public Film(String name, String description, String releaseDate, int duration, Integer rate, Integer mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Long.valueOf(duration);
        if(rate == null || rate < 0){
            this.rate = likes.size();
        }else{
            this.rate = rate + likes.size();
        }
        this.mpa = mpa;
    }

    public Film(String name,
                String description,
                String releaseDate,
                int duration,
                Integer rate,
                HashMap<String, Integer> mpaMap,
                List<HashMap<String, Integer>>genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Long.valueOf(duration);
        if(rate == null || rate < 0){
            this.rate = likes.size();
        }else{
            this.rate = rate + likes.size();
        }
        this.mpaMap = mpaMap;
        this.genres = genres;
    }

    public void addLike(Integer idUser){
        likes.add(idUser);
        this.rate = rate + likes.size();
    }

    public void deleteLike(Integer idUser){
        this.rate = rate - likes.size();
        likes.remove(idUser);
    }

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", name);
        values.put("FILM_DESCRIPTION", description);
        values.put("FILM_RELEASE_DATE", releaseDate);
        values.put("FILM_DURATION", duration);
        values.put("MPA_ID", mpa);
        return values;
    }
}
