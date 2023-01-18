package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

import java.util.HashMap;
import java.util.Map;

@Data
public class Feed {
    Long timestamp;
    Integer userId;
    EventType eventType;
    EventOperation operation;
    Integer entityId;

    Integer eventId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("TIMESTAMP_EVENT", timestamp);
        values.put("USER_ID", userId);
        values.put("EVENT_TYPE", eventType.name());
        values.put("OPERATION", operation.name());
        values.put("ENTITY_ID", entityId);

        return values;
    }
}
