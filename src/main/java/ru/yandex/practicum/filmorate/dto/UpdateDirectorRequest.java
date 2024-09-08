package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDirectorRequest {

    Long id;

    String name;

    public boolean hasName() {
        return isNotBlank(name);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
