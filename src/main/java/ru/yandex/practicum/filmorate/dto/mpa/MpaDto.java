package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaDto {

    Integer id;

    String name;
}
