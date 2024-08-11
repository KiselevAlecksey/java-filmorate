package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> getFriends(Long id);

    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId, Long friendId);

    Collection<User> findAll();

    User get(User user);

    User create(User user);

    User update(User user);
}
