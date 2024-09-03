package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users;

    private final Map<Long, Set<Long>> friends;

    @Override
    public void addFriend(Long userId, Long friendId) {
        friends.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        friends.computeIfAbsent(userId, id -> new HashSet<>()).remove(friendId);
        friends.computeIfAbsent(friendId, id -> new HashSet<>()).remove(userId);
    }

    @Override
    public List<Long> getFriends(Long userId) {
        if (friends == null || friends.isEmpty() || friends.get(userId) == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(friends.get(userId));
    }

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> values() {
        return users.values();
    }

    @Override
    public User remove(User user) {
        return users.remove(user.getId());
    }

    @Override
    public Collection<User> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return users.values().stream()
                .filter((User user) -> ids.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
