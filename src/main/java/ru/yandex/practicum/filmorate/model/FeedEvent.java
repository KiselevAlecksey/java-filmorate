package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedEvent {

    private Long userId;
    private Long entityId;
    private String eventType;
    private String operation;

}
