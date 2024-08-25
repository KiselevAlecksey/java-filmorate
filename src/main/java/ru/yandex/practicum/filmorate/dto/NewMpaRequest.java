package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewMpaRequest {

    Integer id;

    String name;
}
