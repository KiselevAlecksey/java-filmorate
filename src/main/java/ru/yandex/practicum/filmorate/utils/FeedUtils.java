package ru.yandex.practicum.filmorate.utils;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.sql.Timestamp;
import java.time.Instant;

public class FeedUtils<T> {
    protected final NamedParameterJdbcTemplate jdbc;

    public FeedUtils(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void saveFeedEvent(FeedEvent feedEvent) {
        String query = "INSERT INTO user_feed (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (:timestamp, :user_id, :event_type, :operation, :entity_id)";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("timestamp", Timestamp.from(Instant.now()));
        params.addValue("user_id", feedEvent.getUserId());
        params.addValue("event_type", feedEvent.getEventType());
        params.addValue("operation", feedEvent.getOperation());
        params.addValue("entity_id", feedEvent.getEntityId());

        update(query, params);
    }

    public void removeFeedEvent(FeedEvent feedEvent) {
        String query = "DELETE FROM user_feed WHERE user_id = :user_id AND entity_id = :entity_id";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("user_id", feedEvent.getUserId());
        params.addValue("entity_id", feedEvent.getEntityId());

        delete(query, params);
    }

    private void delete(String query, MapSqlParameterSource params) {
        jdbc.update(query, params);
    }

    private void update(String query, MapSqlParameterSource params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }
}
