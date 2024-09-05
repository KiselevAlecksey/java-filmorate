package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    @Override
    public Collection<UserDto> getFriends(Long id) {
        if (id == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return userRepository.findFriendsById(Collections.singletonList(id)).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        if ((userId == null || friendId == null) || (userId == friendId)) {
            throw new NotFoundException("Id должен быть указан и не должен совпадать");
        }

        if (userRepository.findById(userId).isPresent() && userRepository.findById(friendId).isPresent()) {
            userRepository.addFriend(userId, friendId);
            return true;
        }

        throw new NotFoundException(
                "Пользователь с id = "
                        + (userRepository.findById(userId).isPresent() ? userId : friendId) + " не найден"
        );
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId).isPresent() && userRepository.findById(friendId).isPresent()) {
            userRepository.removeFriend(userId, friendId);
            return true;
        }

        throw new NotFoundException(
                "Пользователь с id = "
                        + (userRepository.findById(userId).isPresent() ? userId : friendId) + " не найден"
        );
    }

    @Override
    public Collection<UserDto> getCommonFriends(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId).isPresent() && userRepository.findById(friendId).isPresent()) {

            return userRepository.findFriendsById(Arrays.asList(userId, friendId)).stream()
                    .map(UserMapper::mapToUserDto)
                    .collect(Collectors.toList()
                    );
        }

        throw new NotFoundException(
                "Пользователь с id = "
                        + (userRepository.findById(userId).isPresent() ? userId : friendId) + " не найден"
        );
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.values().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public UserDto get(long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + id + " не существует"));
    }

    @Override
    public List<Film> getFilmRecommendations(Long id) {
        if (id == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
        }

        return filmRepository.getRecommendedFilms(id);
    }

    @Override
    public UserDto create(NewUserRequest userRequest) {

        if (userRequest.getEmail().isBlank() || userRequest.getEmail().indexOf('@') == -1
                || userRequest.getLogin().isBlank() || userRequest.getLogin().indexOf(' ') >= 0
                || userRequest.getBirthday().isAfter(Instant.now())) {

            throw new ConditionsNotMetException(
                    "Поле почта не может быть пустым"
                            + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                            + " дата рождения не может быть в будущем"
            );
        }

        User createUser = UserMapper.mapToUser(userRequest);

        boolean isEmpty = createUser.getName() == null || createUser.getName().isBlank();

        if (isEmpty) {
            return UserMapper.mapToUserDto(
                    userRepository.save(createUser.toBuilder().name(userRequest.getLogin()).build())
            );
        } else {
            return UserMapper.mapToUserDto(userRepository.save(createUser));
        }
    }

    @Override
    public boolean remove(Long id) {
        if (id == null || !userRepository.findById(id).isPresent()) {
            throw new NotFoundException("Id не найден");
        }
        return userRepository.remove(id);
    }

    @Override
    public UserDto update(UpdateUserRequest userRequest) {
        if (userRequest.getId() == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userRequest.getId()).isPresent()) {
            if (userRequest.getEmail().isBlank() || userRequest.getEmail().indexOf('@') == -1
                    || userRequest.getLogin().isBlank() || userRequest.getLogin().indexOf(' ') >= 0
                    || userRequest.getBirthday().isAfter(Instant.now())) {

                throw new ConditionsNotMetException(
                        "Поле почта не может быть пустым"
                                + " или пропущен знак @" + " логин не может быть пустым и содержать пробелы"
                                + " дата рождения не может быть в будущем"
                );
            }

            User updatedUser = userRepository.findById(userRequest.getId())
                    .map(user -> UserMapper.updateUserFields(user, userRequest))
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

            userRepository.update(updatedUser);

            return UserMapper
                    .mapToUserDto(updatedUser.getName().isBlank()
                            ? updatedUser.toBuilder().id(userRequest.getId()).name(userRequest.getLogin()).build() : updatedUser);
        }
        throw new NotFoundException("Пользователь с id = " + userRequest.getId() + " не найден");
    }

    private Set<Long> getListIdsFromUsers(Long userId) {
        return userRepository.getFriends(userId);
    }
}