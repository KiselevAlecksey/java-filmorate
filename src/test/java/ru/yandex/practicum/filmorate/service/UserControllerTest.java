package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Контроллер пользователей")
public class UserControllerTest {
    UserController userController;
    Instant birthday = Instant.ofEpochMilli(0L);
    User defaultUser;
    String defaultName = "user@email.com";
    String defaultLogin = "login";
    String defaultEmail = "name";

    @BeforeEach
    public void init() {
        userController = new UserController();
        defaultUser = new User(1L, "user@email.com", "login", "name", birthday);
    }

    @DisplayName("Должен создать нового пользователя")
    @Test
    public void shouldCreateNewUser() {
        userController.create(defaultUser);
        assertEqualsUser(defaultUser, userController.users.get(defaultUser.getId()), "пользователи не совпадают");
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
        assertEqualsUser(expectedUpdateUser, userController.users.get(defaultUser.getId()), "фильмы не совпадают");
    }

    @DisplayName("Должен вернуть список всех пользователей")
    @Test
    public void shouldReturnListOfUsers() {
        Map<Long, User> expectedUsers = new HashMap<>();
        expectedUsers.put(defaultUser.getId(), defaultUser);
        userController.users.save(defaultUser);
        assertEquals(expectedUsers.size(), userController.findAll().size());
        assertEqualsUser(expectedUsers.get(defaultUser.getId()), userController.users.get(defaultUser.getId()), "фильмы не совпадают");
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
        assertEquals(userController.users.get(expectedUpdateUser.getId()).getName(), defaultUser.getLogin());
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
