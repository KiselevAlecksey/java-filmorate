package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getById(@PathVariable long id) {
        log.error("User get by id {} start", id);
        return userService.getById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody NewUserRequest userRequest) {
        log.error("User create {} start", userRequest);
        UserDto created = userService.create(userRequest);
        log.error("Created user is {}", userRequest.getEmail());
        return created;
    }


    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@RequestBody UpdateUserRequest userRequest) {
        log.error("User update {} start", userRequest);
        UserDto updated = userService.update(userRequest);
        log.error("Updated user is {} complete", userRequest.getEmail());
        return updated;
    }


    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.error("Users add friends id {}, friend_id {} start", id, friendId);
        userService.addFriend(id, friendId);
        log.error("Added friends users id are {}, friend_id {}", id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.error("Users remove friends id {}, friend_id {} start", id, friendId);
        userService.removeFriend(id, friendId);
        log.error("Removed friends users id are {}, friend_id {}", id, friendId);
    }


    @GetMapping("/{id}/friends/common/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long friendId) {
        log.error("Users get common friends id {}, friend_id {} start", id, friendId);
        Collection<UserDto> commonFriends = userService.getCommonFriends(id, friendId);
        log.error("Users get common friends id {}, friend_id {} complete", id, friendId);
        return commonFriends;
    }


    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getFriends(@PathVariable long id) {
        log.error("User get friends list id {} start", id);
        Collection<UserDto> friends = userService.getFriends(id);
        log.error("User get friends list id {} complete", id);
        return friends;
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmRecommendations(@PathVariable Long id) {
        log.info("Get film recommendations request");
        return userService.getFilmRecommendations(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean userRemove(@PathVariable long id) {
        log.error("Users remove user id {},start", id);
        boolean response = userService.remove(id);
        log.error("Users remove user id {},complete", id);
        return response;
    }
}
