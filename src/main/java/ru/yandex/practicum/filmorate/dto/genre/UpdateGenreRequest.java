package ru.yandex.practicum.filmorate.dto.genre;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class UpdateGenreRequest {

    Integer id;

    String name;

    public boolean hasName() {
        return isNotBlank(name);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
