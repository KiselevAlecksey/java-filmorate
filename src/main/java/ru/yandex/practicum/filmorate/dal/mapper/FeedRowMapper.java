package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("FeedRowMapper")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedRowMapper implements RowMapper<Feed> {

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

    public static RowMapper<Feed> getInstance() {
        return new FeedRowMapper();
    }
}
