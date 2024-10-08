package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * User.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
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
