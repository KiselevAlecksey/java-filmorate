package ru.yandex.practicum.filmorate.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest  implements UserRequest {

    String login;

    String email;

    String name;

    Instant birthday;
}
