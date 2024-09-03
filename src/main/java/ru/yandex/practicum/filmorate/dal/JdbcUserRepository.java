package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.UserSql.*;

@Repository
@Qualifier("JdbcUserRepository")
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        update(INSERT_FRIEND, new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId));
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {

        if (findOneByIdInFriends(userId).isPresent()) {
            update(DELETE_FRIEND, new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("friend_id", friendId));
        }

    }

    @Override
    public Set<Long> getFriends(Long userId) {

        Optional<User> user = findOneByIdInFriends(userId);

        if (user.isEmpty()) {
            return Collections.emptySet();
        }

        List<User> list = findMany(FIND_BY_ID_FRIENDS_QUERY, new MapSqlParameterSource().addValue("user_id", userId));

        LinkedHashSet<Long> ids = new LinkedHashSet<>();

        for (User user1 : list) {
            ids.add(user1.getId());
        }

        return ids;
    }

    @Override
    public User save(User user) {

        Long id = insert(INSERT_QUERY, createParameterSource(user));

        if (id != null) {
            user.setId(id);
            return user;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    private static MapSqlParameterSource createParameterSource(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", Timestamp.from(user.getBirthday()));

        return params;
    }

    @Override
    public User update(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", user.getId());
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", Timestamp.from(user.getBirthday()));

        update(UPDATE_QUERY, params);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return findOne(FIND_BY_ID_QUERY_USERS, new MapSqlParameterSource().addValue("id", userId));
    }

    @Override
    public Optional<User> findOneByIdInFriends(Long userId) {

        return findOne(FIND_ALL_FRIENDS, new MapSqlParameterSource().addValue("user_id", userId));
    }

    @Override
    public Collection<User> values() {
        return jdbc.query(FIND_ALL_USERS, mapper);
    }

    @Override
    public boolean remove(User user) {

        Optional<User> optionalUser = findById(user.getId());

        if (optionalUser.isEmpty()) {
            return false;
        }

        return delete(DELETE_USER_QUERY, new MapSqlParameterSource().addValue("id", user.getId()));
    }


    @Override
    public Collection<User> findFriendsById(List<Long> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", ids.getFirst());

        Collection<User> users = findMany(FIND_ALL_FRIENDS, params);

        if (ids.size() > 1) {
            params.addValue("user_id", ids.getLast());
            Collection<User> friends = findMany(FIND_ALL_FRIENDS, params);
            users.retainAll(friends);
        }
        return users;
    }
}
