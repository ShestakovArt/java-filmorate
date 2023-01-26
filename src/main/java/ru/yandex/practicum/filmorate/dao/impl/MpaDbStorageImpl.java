package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorageImpl implements MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String findNameMpa(Integer id) {
        String sqlQuery = String.format("select MPA_NAME " +
                "from MPA where MPA_ID = %d", id);
        List<String> nameList = jdbcTemplate.queryForList(sqlQuery, String.class);

        if (nameList.size() != 1) {
            throw new ValidationException("Не коректный ID MPA");
        }

        return nameList.get(0);
    }

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "select MPA_ID, MPA_NAME from MPA";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("MPA_ID")
                , resultSet.getString("MPA_NAME"));

        return mpa;
    }
}
