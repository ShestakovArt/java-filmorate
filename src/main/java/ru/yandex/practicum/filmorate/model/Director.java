package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Director {

    Integer id;

    @NotNull(message = "name should not be null")
    @NotBlank(message = "name should not be blank")
    String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("DIRECTOR_ID", id);
        values.put("DIRECTOR_NAME", name);

        return values;
    }
}
