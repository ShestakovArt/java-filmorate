package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

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

    public void addLike(Integer idUser){
        if (idUser > 0){
            likes.add(idUser);
            this.rate = rate + likes.size();
        }
    }

    public void deleteLike(Integer idUser){
        if (idUser > 0){
            this.rate = rate - likes.size();
            likes.remove(idUser);
        }
    }
}
