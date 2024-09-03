package ru.yandex.practicum.filmorate.dto.genre;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewGenreRequest {

    Integer id;

    String name;
}
