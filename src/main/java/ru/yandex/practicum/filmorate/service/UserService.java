package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import java.util.Collection;

public interface UserService {

    Collection<UserDto> getFriends(Long id);

    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    Collection<UserDto> getCommonFriends(Long userId, Long friendId);

    Collection<UserDto> findAll();

    UserDto get(long user);

    UserDto create(NewUserRequest userRequest);

    UserDto update(UpdateUserRequest userRequest);

    boolean remove(Long id);
}
