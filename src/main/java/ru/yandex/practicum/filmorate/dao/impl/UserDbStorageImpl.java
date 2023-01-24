package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserDbStorage {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Добавить пользователя
     *
     * @param user данные пользователя
     * @return присвоенный id пользователю
     */
    @Override
    public int addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }

    /**
     * Обновить данные пользователя
     *
     * @param user данные пользователя
     */
    @Override
    public void upgradeUser(User user) {
        String sqlQuery = "update USERS set " +
                "USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ? , USER_BIRTHDAY = ?" +
                "where USER_ID = ?";

        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
    }

    /**
     * Получить данные пользователя
     *
     * @param id id пользователя
     * @return данные пользователя
     */
    @Override
    public Optional<User> findUser(Integer id) {
        String sqlQuery = "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "from USERS where USER_ID = ?";

        return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
    }

    /**
     * Получить информацию по всем пользователям
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        String sqlQuery = "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY from USERS";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    /**
     * Добавить запрос на дружбу
     *
     * @param sender    id пользователя отправителя запроса
     * @param recipient id пользователя получателя запроса
     * @return результат добавления(true/false)
     */
    @Override
    public boolean addRequestsFriendship(Integer sender, Integer recipient) {
        if (!findRequestsFriendship(sender, recipient)) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("SENDER_ID", sender);
            map.put("RECIPIENT_ID", recipient);

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FRIENDSHIP_REQUESTS")
                    .usingColumns("SENDER_ID", "RECIPIENT_ID");

            return simpleJdbcInsert.execute(map) == 1;
        }

        return false;
    }

    /**
     * получить информацию по всем запросам на дружбу пользователя
     *
     * @param userId id пользователя
     * @return Список id друзей пользователя
     */
    @Override
    public List<Integer> findAllFriends(Integer userId) {
        String sqlQuery = String.format("select RECIPIENT_ID as friends\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d", userId);

        return jdbcTemplate.queryForList(sqlQuery, Integer.class);
    }

    /**
     * Удаление запроса на дружбу
     *
     * @param userId   id пользователя отправителя запроса
     * @param idFriend id пользователя получателя запроса
     * @return результат удаления(true/false)
     */
    @Override
    public boolean deleteFriends(Integer userId, Integer idFriend) {
        String sqlQuery = String.format("delete\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d and RECIPIENT_ID = %d", userId, idFriend);

        return jdbcTemplate.update(sqlQuery) > 0;
    }

    /**
     * Удалить пользователя
     *
     * @param userId id пользователя
     * @return результат удаления(true/false)
     */
    @Override
    public boolean deleteUser(Integer userId) {
        String sqlQuery = String.format("delete\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where RECIPIENT_ID = %d", userId);
        jdbcTemplate.update(sqlQuery);

        sqlQuery = String.format("delete\n" +
                "from USERS\n" +
                "where USER_ID = %d", userId);

        return jdbcTemplate.update(sqlQuery) > 0;
    }

    /**
     * Получить 'ленту событий' пользоватлея
     *
     * @param userId id пользователя
     * @return список событий
     */
    @Override
    public Collection<Feed> getFeed(Integer userId) {
        findUser(userId);
        String sqlQuery = String.format("select EVENT_ID, TIMESTAMP_EVENT, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID\n" +
                "from EVENT\n" +
                "where USER_ID = %d", userId);

        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed);
    }

    /**
     * Записать данные по событию пользователя
     *
     * @param userId    id пользователя
     * @param entityId  идентификатор сущности, с которой произошло событие
     * @param type      тип события, одно из значениий LIKE, REVIEW, FRIEND
     * @param operation событие, одно из значениий REMOVE, ADD, UPDATE
     * @return результат (true/false)
     */
    @Override
    public void recordEvent(Integer userId, Integer entityId, EventType type, EventOperation operation) {
        Feed feed = new Feed();
        feed.setTimestamp(new Timestamp(new Date().getTime()).getTime());
        feed.setUserId(userId);
        feed.setEventType(type);
        feed.setOperation(operation);
        feed.setEntityId(entityId);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENT")
                .usingGeneratedKeyColumns("EVENT_ID");
        if (simpleJdbcInsert.execute(feed.toMap()) != 1) {
            throw new IncorrectParameterException("Ошибка при добавлении записи в ленту событий");
        }
    }


    /**
     * Найти запрос на дружбу пользователей
     *
     * @param firstId  id первого пользователя
     * @param secondId id второго пользователя
     * @return наличие запроса на дружбу(true/false)
     */
    private boolean findRequestsFriendship(Integer firstId, Integer secondId) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where (SENDER_ID = %d or RECIPIENT_ID = %d)" +
                " and (SENDER_ID = %d or RECIPIENT_ID = %d)", firstId, firstId, secondId, secondId);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User(resultSet.getString("USER_EMAIL")
                , resultSet.getString("USER_LOGIN")
                , resultSet.getString("USER_NAME")
                , resultSet.getString("USER_BIRTHDAY"));
        user.setId(resultSet.getInt("USER_ID"));

        return user;
    }

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(resultSet.getInt("EVENT_ID"));
        feed.setTimestamp(resultSet.getLong("TIMESTAMP_EVENT"));
        feed.setUserId(resultSet.getInt("USER_ID"));
        feed.setEventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")));
        feed.setOperation(EventOperation.valueOf(resultSet.getString("OPERATION")));
        feed.setEntityId(resultSet.getInt("ENTITY_ID"));

        return feed;
    }
}
