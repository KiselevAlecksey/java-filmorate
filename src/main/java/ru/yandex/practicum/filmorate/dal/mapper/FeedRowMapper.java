package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("FeedRowMapper")
@RequiredArgsConstructor
public class FeedRowMapper implements RowMapper<Feed> {
    protected final NamedParameterJdbcTemplate jdbc;

    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Feed feed = new Feed();
        feed.setTimestamp(resultSet.getTimestamp("timestamp").toInstant());
        feed.setUserId(resultSet.getLong("user_id"));
        feed.setEventType(resultSet.getString("event_type"));
        feed.setOperation(resultSet.getString("operation"));
        feed.setEventId(resultSet.getLong("event_id"));
        feed.setEntityId(resultSet.getLong("entity_id"));

        return feed;
    }
}
