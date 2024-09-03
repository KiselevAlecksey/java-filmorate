package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmRepository;
import ru.yandex.practicum.filmorate.repository.InMemoryUserRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.DefaultUserService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Контроллер пользователей")
@RequiredArgsConstructor
public class UserControllerTest {
    UserController userController;
    Instant birthday = Instant.ofEpochMilli(0L);
    User defaultUser;
    String defaultName = "user@email.com";
    String defaultLogin = "login";
    String defaultEmail = "name";

    private final FilmRepository filmRepository = new InMemoryFilmRepository(
            new HashMap<>(), new HashMap<>(), new ArrayList<>()
    );

    private final UserRepository userRepository = new InMemoryUserRepository(new HashMap<>(), new HashMap<>());

    private final UserService userService = new DefaultUserService(userRepository);

    @BeforeEach
    public void init() {
        userController = new UserController(userService);
        defaultUser = new User(1L, "user@email.com", "login", "name", birthday);
    }

    @DisplayName("Должен создать нового пользователя")
    @Test
    public void shouldCreateNewUser() {
        userController.create(defaultUser);
        assertEqualsUser(defaultUser, userRepository.findById(defaultUser.getId()), "пользователи не совпадают");
    }

    @DisplayName("Должен обновить данные пользователя")
    @Test
    public void shouldUpdateUser() {
        User expectedUpdateUser = defaultUser.toBuilder()
                .email("updateuser@email.com")
                .login("newLogin")
                .name("newName")
                .birthday(Instant.ofEpochMilli(123456789L))
                .build();
        userController.create(defaultUser);
        userController.update(expectedUpdateUser);
        assertEqualsUser(expectedUpdateUser, userRepository.findById(defaultUser.getId()), "фильмы не совпадают");
    }

    @DisplayName("Должен вернуть список всех пользователей")
    @Test
    public void shouldReturnListOfUsers() {
        Map<Long, User> expectedUsers = new HashMap<>();
        expectedUsers.put(defaultUser.getId(), defaultUser);
        userRepository.save(defaultUser);
        assertEquals(expectedUsers.size(), userController.findAll().size());
        assertEqualsUser(expectedUsers.get(defaultUser.getId()), userRepository.findById(defaultUser.getId()), "фильмы не совпадают");
    }

    @DisplayName("Не должен добавлять нового пользователя c пустой почтой")
    @Test
    public void shouldNotCreateNewUserWithBlankEmail() {
        User expectedUpdateUser = defaultUser.toBuilder()
                .email(" ")
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            userController.create(expectedUpdateUser);
        });
        String expectedMessage = "Поле почта не может быть пустым"
                + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                + " дата рождения не может быть в будущем";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("Не должен обновлять нового пользователя c пустой почтой")
    @Test
    public void shouldNotCreateNewUserWithBlankLogin() {
        User expectedUpdateUser = defaultUser.toBuilder()
                .login("")
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            userController.create(expectedUpdateUser);
        });
        String expectedMessage = "Поле почта не может быть пустым"
                + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                + " дата рождения не может быть в будущем";
        assertEquals(expectedMessage, thrown.getMessage());

        User expectedUpdateUser2 = defaultUser.toBuilder()
                .login(" ")
                .build();
        thrown = assertThrows(ConditionsNotMetException.class, () -> {
            userController.create(expectedUpdateUser2);
        });

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("Должен заменить пустое имя на логин")
    @Test
    public void shouldReplaceBlankNameWithLogin() {
        User expectedUpdateUser = defaultUser.toBuilder()
                .name("")
                .build();
        userController.create(expectedUpdateUser);
        assertEquals(userRepository.findById(expectedUpdateUser.getId()).getName(), defaultUser.getLogin());
    }

    @DisplayName("Не должен создавать нового пользователя с датой рождения в будущем")
    @Test
    public void shouldNotCreateNewUserWithBirthdayInFuture() {
        User expectedUser = defaultUser.toBuilder()
                .birthday(Instant.now().plusMillis(1000))
                .build();
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            userController.create(expectedUser);
        });
        String expectedMessage = "Поле почта не может быть пустым"
                + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                + " дата рождения не может быть в будущем";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("Не должен обновлять нового пользователя с датой рождения в будущем")
    @Test
    public void shouldNotUpdateNewUserWithBirthdayInFuture() {
        User expectedUpdateUser = defaultUser.toBuilder()
                .birthday(Instant.now().plusMillis(1000))
                .build();
        userController.create(defaultUser);
        ConditionsNotMetException thrown = assertThrows(ConditionsNotMetException.class, () -> {
            userController.update(expectedUpdateUser);
        });
        String expectedMessage = "Поле почта не может быть пустым"
                + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                + " дата рождения не может быть в будущем";
        assertEquals(expectedMessage, thrown.getMessage());
    }

    private void assertEqualsUser(User expected, User actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getEmail(), actual.getEmail(), message + ", email");
        assertEquals(expected.getLogin(), actual.getLogin(), message + ", login");
        assertEquals(expected.getBirthday(), actual.getBirthday(), message + ", birthday");
    }
}
