package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.feed.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedMapper {

    public static FeedDto mapToFeedDto(Feed feed) {
        FeedDto dto = new FeedDto();
        dto.setUserId(feed.getUserId());
        dto.setEventType(feed.getEventType());
        dto.setOperation(feed.getOperation());
        dto.setEventId(feed.getEventId());
        dto.setEntityId(feed.getEntityId());
        dto.setTimestamp(feed.getTimestamp().toEpochMilli());

        return dto;
    }
}
