package ru.yandex.practicum.filmorate.dto.user;

public interface UserRequest {

    String getLogin();

    String getEmail();

    String getName();

    java.time.Instant getBirthday();

    void setLogin(String login);

    void setEmail(String email);

    void setName(String name);

    void setBirthday(java.time.Instant birthday);
}
