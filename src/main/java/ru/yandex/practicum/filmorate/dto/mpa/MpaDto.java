package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class MpaDto {

    Integer id;

    String name;
}
