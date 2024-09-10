package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcUserRepository.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepository")
class JdbcUserRepositoryTest {
    public static final long TEST_USER_ID = 2L;
    public static final long TEST_FRIEND_ID = 3L;

    private final JdbcUserRepository userRepository;

    @Test
    @DisplayName("должен добавлять друга")
    void should_add_friend() {

        User user1 = userRepository.save(getTestUser());
        User user2 = userRepository.save(getTestUser());

        userRepository.addFriend(user2.getId(), user1.getId());
        userRepository.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = userRepository.findFriendsById(Collections.singletonList(user1.getId()));

        assertThat(friends).contains(user2);
    }

    @Test
    @DisplayName("должен удалять друга")
    void should_remove_friend() {
        User user1 = userRepository.save(getTestUser());
        User user2 = userRepository.save(getTestUser());

        Collection<User> users = userRepository.values();

        userRepository.addFriend(user2.getId(), user1.getId());
        userRepository.addFriend(user1.getId(), user2.getId());

        userRepository.removeFriend(TEST_USER_ID, TEST_FRIEND_ID);

        Set<Long> friends = userRepository.getFriends(TEST_USER_ID);

        assertThat(friends).doesNotContain(TEST_FRIEND_ID);
    }

    @Test
    @DisplayName("должен сохранять пользователя")
    void should_save_user() {
        User user = getTestUser();

        userRepository.save(user);
        assertThat(userRepository.findById(user.getId()))
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).usingRecursiveComparison().isEqualTo(user));
    }

    @Test
    @DisplayName("должен обновлять пользователя")
    void should_update_user() {
        User user = userRepository.save(getTestUser());

        user.setName("updated name");
        userRepository.update(user);
        assertThat(userRepository.findById(user.getId()))
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("updated name"));
    }

    @Test
    @DisplayName("должен возвращать пользователя по идентификатору")
    void should_return_user_when_find_by_id() {
        User user = userRepository.save(getTestUser());

        assertThat(userRepository.findById(TEST_USER_ID))
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getId()).isEqualTo(TEST_USER_ID));
    }

    @Test
    @DisplayName("должен возвращать пользователя из друзей по идентификатору")
    void should_return_user_from_friends_when_find_by_id() {
        User user1 =  userRepository.save(getTestUser());
        User user2 = userRepository.save(getTestUser());

        userRepository.addFriend(user1.getId(), user2.getId());
        userRepository.addFriend(user2.getId(), user1.getId());

        assertThat(userRepository.getByIdInFriends(user1.getId()))
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getId()).isEqualTo(user2.getId()));
    }

    @Test
    @DisplayName("должен возвращать всех пользователей")
    void should_return_all_users() {
        User user = userRepository.save(getTestUser());

        Collection<User> users = userRepository.values();
        assertThat(users).isNotEmpty()
                .extracting(User::getId)
                .contains(user.getId(), 1L);
    }

    @Test
    @DisplayName("должен удалять пользователя")
    void should_remove_user() {
        User user = getTestUser();

        userRepository.save(user);
        userRepository.remove(user.getId());
        assertThat(userRepository.findById(TEST_USER_ID)).isNotPresent();
    }

    private static User getTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("example@email.ru")
                .name("name")
                .login("description")
                .birthday(Instant.ofEpochMilli(1_714_608_000_000L))
                .build();
    }
}