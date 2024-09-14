package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class NewMpaRequest {

    Integer id;

    String name;
}
