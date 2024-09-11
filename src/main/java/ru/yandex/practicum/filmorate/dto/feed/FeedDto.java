package ru.yandex.practicum.filmorate.dto.feed;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@JsonPropertyOrder({"timestamp", "userId", "eventType", "operation", "eventId", "entityId"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "eventId")
public class FeedDto {

    Long timestamp;

    Long userId;

    String eventType;

    String operation;

    Long eventId;

    Long entityId;
}
