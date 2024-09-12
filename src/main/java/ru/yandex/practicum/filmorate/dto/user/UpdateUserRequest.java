package ru.yandex.practicum.filmorate.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest implements UserRequest {

    Long id;

    String login;

    String email;

    String name;

    Instant birthday;

    public boolean hasName() {
        return isNotBlank(name);
    }

    public boolean hasEmail() {
        return isNotBlank(email);
    }

    public boolean hasLogin() {
        return isNotBlank(login);
    }

    public boolean hasBirthday() {
        return birthday != null && birthday.isBefore(Instant.now());
    }

    boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
