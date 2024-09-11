package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "eventId")
public class Feed {

    @Builder.Default
    Instant timestamp = Instant.ofEpochMilli(0L);

    Long userId;

    String eventType;

    String operation;

    Long eventId;

    Long entityId;
}
