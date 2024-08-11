package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository {
    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Long> getFriends(Long userId);

    void save(User user);

    User findById(Long id);

    Collection<User> values();

    User remove(User user);

    Collection<User> findAllById(List<Long> ids);
}
