package ru.yandex.practicum.filmorate.dto.feed;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "eventId")
public class NewFeedRequest {

    Instant timestamp;

    Long userId;

    String eventType;

    String operation;

    Long eventId;

    Long entityId;
}
