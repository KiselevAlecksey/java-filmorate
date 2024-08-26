package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.UserSql.*;

@Repository
@Qualifier("JdbcUserRepository")
public class JdbcUserRepository extends BaseRepository implements UserRepository {

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("friend_id", friendId);

        update(INSERT_FRIEND, params);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("friend_id", friendId);

        if (findOneByIdInFriends(userId).isPresent()) {
            update(DELETE_FRIEND, params);
        }

    }

    @Override
    public Set<Long> getFriends(Long userId) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        List<Long> list = findMany(FIND_BY_ID_FRIENDS_QUERY, params);

        return new HashSet<>(list);
    }

    @Override
    public User save(User user) {

        Map<String, Object> params = new HashMap<>();

        params.put("email", user.getEmail());
        params.put("login", user.getLogin());
        params.put("name", user.getName());
        params.put("birthday", Timestamp.from(user.getBirthday()));

        Long id = insert(INSERT_QUERY, params);

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
        Map<String, Object> params = new HashMap<>();

        params.put("id", user.getId());
        params.put("email", user.getEmail());
        params.put("login", user.getLogin());
        params.put("name", user.getName());
        params.put("birthday", Timestamp.from(user.getBirthday()));

        update(UPDATE_QUERY, params);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        return findOne(FIND_BY_ID_QUERY_USERS, params);
    }

    @Override
    public Optional<User> findOneByIdInFriends(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        return findOne(FIND_ALL_FRIENDS, params);
    }

    @Override
    public Collection<User> values() {
        return jdbc.query(FIND_ALL_USERS, mapper);
    }

    @Override
    public boolean remove(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user.getId());
        return delete(DELETE_USER_QUERY, params);
    }


    @Override
    public Collection<User> findFriendsById(List<Long> ids) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", ids.getFirst());
        Collection<User> users = findMany(FIND_ALL_FRIENDS, params);

        if (ids.size() > 1) {
            params.remove("user_id");
            params.put("user_id", ids.getLast());
            Collection<User> friends = findMany(FIND_ALL_FRIENDS, params);
            users.retainAll(friends);
        }
        return users;
    }
}
