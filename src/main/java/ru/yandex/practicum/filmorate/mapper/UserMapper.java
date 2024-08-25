package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dto.user.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static User mapToUser(NewUserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .login(request.getLogin())
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setBirthday(user.getBirthday());
        return dto;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {

        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }
        return user;
    }
}
