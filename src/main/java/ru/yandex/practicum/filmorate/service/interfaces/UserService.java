package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dto.feed.FeedDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.List;


public interface UserService {

    Collection<UserDto> getFriends(Long id);

    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    Collection<UserDto> getCommonFriends(Long userId, Long friendId);

    Collection<UserDto> findAll();

    UserDto getById(long id);

    List<Film> getFilmRecommendations(Long id);

    UserDto create(NewUserRequest userRequest);

    UserDto update(UpdateUserRequest userRequest);

    boolean remove(Long id);

    Collection<FeedDto> getFeed(Long id);
}
