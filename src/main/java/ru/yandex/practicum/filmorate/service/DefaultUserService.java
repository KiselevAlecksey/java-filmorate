package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    long currentMaxId = 0;

    @Override
    public Collection<User> getFriends(Long id) {
        if (id == null || userRepository.findById(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return userRepository.findAllById(userRepository.getFriends(id));
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        if ((userId == null || friendId == null) || (userId == friendId)) {
            throw new NotFoundException("Id должен быть указан и не должен совпадать");
        }

        if (userRepository.findById(userId) != null & userRepository.findById(friendId) != null) {
            userRepository.addFriend(userId, friendId);
            return true;
        }

        throw new NotFoundException(
                "Пользователь с id = "
                        + (userRepository.findById(userId) == null ? userId : friendId) + " не найден"
        );
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId) != null & userRepository.findById(friendId) != null) {
            userRepository.removeFriend(userId, friendId);
            return true;
        }

        throw new NotFoundException(
                "Пользователь с id = "
                        + (userRepository.findById(userId) == null ? userId : friendId) + " не найден"
        );
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId) != null & userRepository.findById(friendId) != null) {

            List<Long> usersIdList = userRepository.getFriends(userId);
            usersIdList.retainAll(userRepository.getFriends(friendId));

            return userRepository.findAllById(usersIdList);
        }

        throw new NotFoundException(
                "Пользователь с id = "
                        + (userRepository.findById(userId) == null ? userId : friendId) + " не найден"
        );
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.values();
    }

    @Override
    public User get(User user) {
        return userRepository.findById(user.getId());
    }

    @Override
    public User create(User user) {

        if (user.getEmail().isBlank() || user.getEmail().indexOf('@') == -1
                || user.getLogin().isBlank() || user.getLogin().indexOf(' ') >= 0
                || user.getBirthday().isAfter(Instant.now())) {

            throw new ConditionsNotMetException(
                    "Поле почта не может быть пустым"
                    + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                    + " дата рождения не может быть в будущем"
            );
        }

        User createUser = User.builder()
                .id(getNextId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();

        boolean isEmpty = createUser.getName() == null || createUser.getName().isBlank();

        if (isEmpty) {
            userRepository.save(createUser.toBuilder().name(user.getLogin()).build());
        } else {
            userRepository.save(createUser);
        }

        return isEmpty ? createUser.toBuilder().name(user.getLogin()).build() : createUser;
    }

    @Override
    public User update(User user) {

        if (user.getId() == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(user.getId()) != null) {
            if (user.getEmail().isBlank() || user.getEmail().indexOf('@') == -1
                    || user.getLogin().isBlank() || user.getLogin().indexOf(' ') >= 0
                    || user.getBirthday().isAfter(Instant.now())) {

                throw new ConditionsNotMetException(
                        "Поле почта не может быть пустым"
                                + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                                + " дата рождения не может быть в будущем"
                );
            }

            User updateUser = user.toBuilder()
                    .email(user.getEmail())
                    .login(user.getLogin())
                    .name(user.getName())
                    .birthday(user.getBirthday())
                    .build();

            userRepository.save(updateUser);

            return updateUser.getName().isBlank() ? updateUser.toBuilder().name(user.getLogin()).build() : updateUser;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
