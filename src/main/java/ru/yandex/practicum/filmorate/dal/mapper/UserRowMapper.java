package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {

    protected final NamedParameterJdbcTemplate jdbc;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));

        Timestamp birthday = resultSet.getTimestamp("birthday");
        user.setBirthday(birthday.toInstant());

        return user;
    }
}
