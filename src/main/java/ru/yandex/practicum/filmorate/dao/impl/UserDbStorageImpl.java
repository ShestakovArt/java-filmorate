package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class UserDbStorageImpl implements UserDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorageImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int addUser(User user){
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
    public boolean addRequestsFriendship (Integer sender, Integer recipient){
        if(!findRequestsFriendship(sender, recipient)){
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

    @Override
    public List<Integer> findAllFriends(Integer idUser) {
        String sqlQuery = String.format("select RECIPIENT_ID as friends\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d", idUser);

        return jdbcTemplate.queryForList(sqlQuery, Integer.class);
    }

    @Override
    public boolean deleteFriends(Integer idUser, Integer idFriend) {
        String sqlQuery = String.format("delete\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where SENDER_ID = %d and RECIPIENT_ID = %d", idUser, idFriend);

        return jdbcTemplate.update(sqlQuery) > 0;
    }

    @Override
    public boolean deleteUser(Integer idUser) {
        String sqlQuery = String.format("delete\n" +
                "from FRIENDSHIP_REQUESTS\n" +
                "where RECIPIENT_ID = %d", idUser);
        jdbcTemplate.update(sqlQuery);

         sqlQuery = String.format("delete\n" +
                "from USERS\n" +
                "where USER_ID = %d", idUser);
        return jdbcTemplate.update(sqlQuery) > 0;
    }


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
}
