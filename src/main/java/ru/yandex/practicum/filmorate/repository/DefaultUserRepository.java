package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DefaultUserRepository implements UserRepository {

    private final Map<Long, User> users;

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
}
