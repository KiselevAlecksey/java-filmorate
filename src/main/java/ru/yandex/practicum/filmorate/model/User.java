package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * User.
 */
@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    Long id;
    @Email
    String email;
    @NotNull
    String login;
    String name;
    @Builder.Default
    Instant birthday = Instant.ofEpochMilli(0L);
}
