package ru.yandex.practicum.filmorate.service;

import java.time.Instant;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    UserRepository users = new UserRepository();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        logger.debug("User {} check validation", user);
        if (user.getEmail().isBlank() || user.getEmail().indexOf('@') == -1
                || user.getLogin().isBlank() || user.getLogin().indexOf(' ') >= 0
                || user.getBirthday().isAfter(Instant.now())) {

            logger.trace("Поле {} почта не может быть пустым"
                    + " или пропущен знак @" + ", логин {} не может быть пустым и содержать пробелы"
                    + ", дата рождения {} не может быть в будущем",
                    user.getEmail(), user.getLogin(), user.getBirthday());

            throw new ConditionsNotMetException("Поле почта не может быть пустым"
                    + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                    + " дата рождения не может быть в будущем");
        }
        logger.trace("Created a new user");
        User createUser = User.builder()
                .id(getNextId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
        logger.debug("Created user is {}", createUser);
        boolean isEmpty = createUser.getName() == null || createUser.getName().isBlank();
        if (isEmpty) {
          //  User blankName = createUser.toBuilder().name(user.getLogin()).build();
            users.save(createUser.toBuilder().name(user.getLogin()).build());
        } else {
            users.save(createUser);
        }
        logger.trace("Added user");
        return isEmpty ? createUser.toBuilder().name(user.getLogin()).build() : createUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        logger.debug("User {} check validation", user);
        if (user.getId() == null) {
            logger.trace("Id = {} should be pointed", (Object) null);
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (users.findById(user.getId()) != null) {
            if (user.getEmail().isBlank() || user.getEmail().indexOf('@') == -1
                    || user.getLogin().isBlank() || user.getLogin().indexOf(' ') >= 0
                    || user.getBirthday().isAfter(Instant.now())) {

                logger.trace("Поле {} почта не может быть пустым"
                                + " или пропущен знак @" + " логин {} не может быть пустым и содержать пробелы"
                                + " дата рождения {} не может быть в будущем",
                        user.getEmail(), user.getLogin(), user.getBirthday());

                throw new ConditionsNotMetException("Поле почта не может быть пустым"
                        + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                        + " дата рождения не может быть в будущем");
            }
            logger.trace("Updated film");
            User updateUser = user.toBuilder()
                    .email(user.getEmail())
                    .login(user.getLogin())
                    .name(user.getName())
                    .birthday(user.getBirthday())
                    .build();
            logger.debug("Updated user is {} ", updateUser);
            users.save(updateUser);
            logger.trace("Updated user");

            return updateUser.getName().isBlank() ? updateUser.toBuilder().name(user.getLogin()).build() : updateUser;
        }
        logger.trace("User with id = {} not found", user.getId());
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    private long getNextId() {
        logger.trace("Created new id");
        long currentMaxId = 0;
        logger.trace("New id is {} ", currentMaxId);
        return ++currentMaxId;
    }

}
