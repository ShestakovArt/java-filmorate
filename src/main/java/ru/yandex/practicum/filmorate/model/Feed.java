package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

import java.util.HashMap;
import java.util.Map;

@Data
public class Feed {
    /*
        "timestamp": 123344556,
        "userId": 123,
        "eventType": "LIKE", // одно из значениий LIKE, REVIEW или FRIEND
		"operation": "REMOVE", // одно из значениий REMOVE, ADD, UPDATE
        "eventId": 1234, //primary key
        "entityId": 1234   // идентификатор сущности, с которой произошло событие
        */
    Long timestamp;
    Integer userId;
    EventType eventType;
    EventOperation operation;
    Integer entityId;

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
