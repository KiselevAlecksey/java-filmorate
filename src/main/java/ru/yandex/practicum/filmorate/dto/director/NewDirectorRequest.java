package ru.yandex.practicum.filmorate.dto.director;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewDirectorRequest {

    Long id;

    String name;
}
