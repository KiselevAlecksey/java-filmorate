package ru.yandex.practicum.filmorate.dal.interfaces;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getFriends(Long userId);

    User save(User user);

    User update(User user);

    Optional<User> findById(Long userId);

    Integer getByIdInFriends(Long userId, Long friendId);

    Collection<User> values();

    boolean remove(Long id);

    Collection<User> findFriendsById(List<Long> ids);

    Collection<Feed> getFeed(long id);
}
