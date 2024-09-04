package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final UserService userService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto get(@PathVariable long id) {
        return userService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody NewUserRequest userRequest) {
        logger.error("User create {} start", userRequest);
        UserDto created = userService.create(userRequest);
        logger.error("Created user is {}", userRequest.getEmail());
        return created;
    }


    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@RequestBody UpdateUserRequest userRequest) {
        logger.error("User update {} start", userRequest);
        UserDto updated = userService.update(userRequest);
        logger.error("Updated user is {} complete", userRequest.getEmail());
        return updated;
    }


    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        logger.error("Users add friends id {}, friend_id {} start", id, friendId);
        userService.addFriend(id, friendId);
        logger.error("Added friends users id are {}, friend_id {}", id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        logger.error("Users remove friends id {}, friend_id {} start", id, friendId);
        userService.removeFriend(id, friendId);
        logger.error("Removed friends users id are {}, friend_id {}", id, friendId);
    }


    @GetMapping("/{id}/friends/common/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long friendId) {
        logger.error("Users get common friends id {}, friend_id {} start", id, friendId);
        Collection<UserDto> commonFriends = userService.getCommonFriends(id, friendId);
        logger.error("Users get common friends id {}, friend_id {} complete", id, friendId);
        return commonFriends;
    }


    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getFriends(@PathVariable long id) {
        logger.error("User get friends list id {} start", id);
        Collection<UserDto> friends = userService.getFriends(id);
        logger.error("User get friends list id {} complete", id);
        return friends;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean userRemove(@PathVariable long id) {
        logger.error("Users remove user id {},start", id);
        boolean response = userService.remove(id);
        logger.error("Users remove user id {},complete", id);
        return response;
    }
}
