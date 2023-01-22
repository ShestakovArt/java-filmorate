package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Mpa {
    public int id;
    public String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("MPA_ID", id);
        values.put("MPA_NAME", name);

        return values;
    }
}
