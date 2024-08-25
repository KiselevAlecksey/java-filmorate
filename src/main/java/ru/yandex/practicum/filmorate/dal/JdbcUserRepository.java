package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.UserSql.*;

@Repository
@Qualifier("JdbcUserRepository")
public class JdbcUserRepository extends BaseRepository implements UserRepository {

    public JdbcUserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        update(INSERT_FRIEND, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {

        if (findByIdInFriends(userId).isPresent()) {
            update(DELETE_FRIEND, userId, friendId);
        }

    }

    @Override
    public Set<Long> getFriends(Long userId) {
        List<Long> list = findMany(FIND_BY_ID_FRIENDS_QUERY, userId);
        return new HashSet<>(list);
    }

    @Override
    public User save(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(user.getBirthday())
        );
        user.setId(id);

        if (id != null) {
            user.setId(id);
            return user;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return findOne(FIND_BY_ID_QUERY_USERS, userId);
    }

    @Override
    public Optional<User> findByIdInFriends(Long userId) {
        return findOne(FIND_ALL_FRIENDS, userId);
    }

    @Override
    public Collection<User> values() {
        return findMany(FIND_ALL_USERS);
    }

    @Override
    public boolean remove(User user) {
        return delete(DELETE_USER_QUERY, user.getId());
    }


    @Override
    public Collection<User> findFriendsById(List<Long> ids) {
        if (ids.size() < 2) {
            return findMany(FIND_ALL_FRIENDS, ids.getFirst());
        } else {
            Collection<User> users = findMany(FIND_ALL_FRIENDS, ids.getFirst());
            Collection<User> friends = findMany(FIND_ALL_FRIENDS, ids.getLast());
            users.retainAll(friends);
            return users;
        }
    }
}
