package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.enums.EventOperation.ADD;
import static ru.yandex.practicum.filmorate.enums.EventType.FRIEND;

@Component
public class UserDbStorageImpl implements UserDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }

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

    @Override
    public Optional<User> findUser(Integer id) {
        String sqlQuery = "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "from USERS where USER_ID = ?";

        return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY from USERS";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public boolean addRequestsFriendship(Integer sender, Integer recipient) {
        boolean resultOperation = false;
        if (!findRequestsFriendship(sender, recipient)) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("SENDER_ID", sender);
            map.put("RECIPIENT_ID", recipient);

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FRIENDSHIP_REQUESTS")
                    .usingColumns("SENDER_ID", "RECIPIENT_ID");
            resultOperation = simpleJdbcInsert.execute(map) == 1;
            if (resultOperation) {
                resultOperation = recordEvent(sender, recipient);
            }
        }

        return resultOperation;
    }

    @Override
    public List<Integer> findAllFriends(Integer userId) {
        String sqlQuery = String.format("select RECIPIENT_ID as friends\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d", userId);

        return jdbcTemplate.queryForList(sqlQuery, Integer.class);
    }

    @Override
    public boolean deleteFriends(Integer userId, Integer idFriend) {
        String sqlQuery = String.format("delete\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d and RECIPIENT_ID = %d", userId, idFriend);

        return jdbcTemplate.update(sqlQuery) > 0;
    }

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


    private boolean findRequestsFriendship(Integer firstId, Integer secondId) {
        String sqlQuery = String.format("select COUNT(*)\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where (SENDER_ID = %d or RECIPIENT_ID = %d)" +
                " and (SENDER_ID = %d or RECIPIENT_ID = %d)", firstId, firstId, secondId, secondId);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private boolean recordEvent(Integer userId, Integer entityId) {
        Feed feed = new Feed();
        feed.setTimestamp(new Timestamp(new Date().getTime()).getTime());
        feed.setUserId(userId);
        feed.setEventType(FRIEND);
        feed.setOperation(ADD);
        feed.setEntityId(entityId);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENT")
                .usingGeneratedKeyColumns("EVENT_ID");
        return simpleJdbcInsert.executeAndReturnKey(feed.toMap()).intValue() == 1;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User(resultSet.getString("USER_EMAIL")
                , resultSet.getString("USER_LOGIN")
                , resultSet.getString("USER_NAME")
                , resultSet.getString("USER_BIRTHDAY"));
        user.setId(resultSet.getInt("USER_ID"));

        return user;
    }
}
