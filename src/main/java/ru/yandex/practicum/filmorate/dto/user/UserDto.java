package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @Email
    String email;

    @NotNull
    String login;

    String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant birthday;

}
