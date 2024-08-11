package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User create(@RequestBody User user) {
        logger.error("User create {} start", user);
        User created = userService.create(user);
        logger.error("Created user is {}", userService.get(user));
        return created;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User update(@RequestBody @Valid User user) {
        logger.error("User update {} start", user);
        User updated = userService.update(user);
        logger.error("Updated user is {} complete", userService.get(user));
        return updated;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        logger.error("Users add friends id {}, friend_id {} start", id, friendId);
        userService.addFriend(id, friendId);
        logger.error("Added friends users id are {}, friend_id {}", id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        logger.error("Users remove friends id {}, friend_id {} start", id, friendId);
        userService.removeFriend(id, friendId);
        logger.error("Removed friends users id are {}, friend_id {}", id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends/common/{friendId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long friendId) {
        logger.error("Users get common friends id {}, friend_id {} start", id, friendId);
        Collection<User> commonFriends = userService.getCommonFriends(id, friendId);
        logger.error("Users get common friends id {}, friend_id {} complete", id, friendId);
        return commonFriends;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends")
    public Collection<User> get(@PathVariable long id) {
        logger.error("User get friends list id {} start", id);
        Collection<User> friends = userService.getFriends(id);
        logger.error("User get friends list id {} complete", id);
        return friends;
    }
}
