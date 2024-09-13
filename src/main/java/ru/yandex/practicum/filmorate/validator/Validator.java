package ru.yandex.practicum.filmorate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.interfaces.*;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Constant;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Validator {

    @Autowired
    @Qualifier("JdbcGenreRepository")
    private final GenreRepository genreRepository;

    @Autowired
    @Qualifier("JdbcMpaRepository")
    private final MpaRepository mpaRepository;

    @Autowired
    @Qualifier("JdbcReviewRepository")
    private final ReviewRepository reviewRepository;

    @Autowired
    @Qualifier("JdbcFilmRepository")
    private final FilmRepository filmRepository;

    @Autowired
    @Qualifier("JdbcUserRepository")
    private final UserRepository userRepository;

    public void validateUserRequest(UserRequest userRequest) {
        StringBuilder errorMessage = new StringBuilder();

        if (isEmailInvalid(userRequest.getEmail())) {
            errorMessage.append("Поле почта не может быть пустым или пропущен знак @.\n");
        }

        if (isLoginInvalid(userRequest.getLogin())) {
            errorMessage.append("Логин не может быть пустым и содержать пробелы.\n");
        }

        if (isBirthdayInvalid(userRequest.getBirthday())) {
            errorMessage.append("Дата рождения не может быть в будущем.\n");
        }

        if (!errorMessage.isEmpty()) {
            throw new ConditionsNotMetException(errorMessage.toString().trim());
        }
    }

    private boolean isEmailInvalid(String email) {
        return email == null || email.isBlank() || email.indexOf('@') == -1;
    }

    private boolean isLoginInvalid(String login) {
        return login == null || login.isBlank() || login.indexOf(' ') >= 0;
    }

    private boolean isBirthdayInvalid(Instant birthday) {
        return birthday == null || birthday.isAfter(Instant.now());
    }

    public void validateReviewRequest(ReviewRequest reviewRequest) {
        StringBuilder errorMessage = new StringBuilder();

        if (isContentInvalid(reviewRequest)) {
            throw new ConditionsNotMetException("Неверное содержание отзыва");
        }

        if (isUserInvalid(reviewRequest)) {
            errorMessage.append("Неверный id пользователя, ");
        }

        if (isFilmInvalid(reviewRequest)) {
            errorMessage.append("Неверный id фильма, ");
        }

        if (isIsPositiveInvalid(reviewRequest)) {
            throw new ConditionsNotMetException("Отсутствует поле оценка");
        }

        if (!errorMessage.isEmpty()) {
            throw new NotFoundException(errorMessage.toString().trim());
        }

    }

    private boolean isIsPositiveInvalid(ReviewRequest reviewRequest) {
        return reviewRequest.getIsPositive() == null;
    }

    private boolean isContentInvalid(ReviewRequest reviewRequest) {
        return reviewRequest.getContent() == null || reviewRequest.getContent().isBlank();
    }

    private boolean isUserInvalid(ReviewRequest reviewRequest) {
        Long id = reviewRequest.getUserId();

        if (id == null) {
            throw new ConditionsNotMetException("Неверный id пользователя");
        }

        if (id < 1) {
            throw new NotFoundException("Пользователь не найден");
        }

        return userRepository.findById(id).isEmpty();
    }

    private boolean isFilmInvalid(ReviewRequest reviewRequest) {
        Long filmId = reviewRequest.getFilmId();

        if (filmId == null) {
            throw new ConditionsNotMetException("Неверный id фильма");
        }

        if (filmId < 0) {
            throw new NotFoundException("Фильм не найден");
        }

        return filmRepository.getByIdPartialDetails(filmId).isEmpty();
    }

    public void validateFilmRequest(FilmRequest filmRequest) {
        StringBuilder errorMessage = new StringBuilder();

        if (isNameBlank(filmRequest)) {
            errorMessage.append("Название не может быть пустым, ");
        }
        if (isDescriptionTooLong(filmRequest)) {
            errorMessage.append("Описание не может быть больше ").append(Constant.MAX_LENGTH_DESCRIPTION).append(", ");
        }
        if (isReleaseDateInvalid(filmRequest)) {
            errorMessage.append("Дата релиза не может быть раньше ").append(Constant.CINEMA_BURN_DAY).append(", ");
        }
        if (isDurationNegative(filmRequest)) {
            errorMessage.append("Продолжительность не может быть отрицательной, ");
        }
        if (isMpaInvalid(filmRequest)) {
            errorMessage.append("Некорректный рейтинг MPA, ");
        }
        if (areGenresInvalid(filmRequest)) {
            errorMessage.append("Некорректные жанры, ");
        }

        if (!errorMessage.isEmpty()) {
            throw new ConditionsNotMetException(errorMessage.toString().trim());
        }
    }

    private boolean isNameBlank(FilmRequest filmRequest) {
        return filmRequest.getName().isBlank();
    }

    private boolean isDescriptionTooLong(FilmRequest filmRequest) {
        return filmRequest.getDescription().length() > Constant.MAX_LENGTH_DESCRIPTION;
    }

    private boolean isReleaseDateInvalid(FilmRequest filmRequest) {
        return filmRequest.getReleaseDate().isBefore(Instant.ofEpochMilli(Constant.CINEMA_BURN_DAY));
    }

    private boolean isDurationNegative(FilmRequest filmRequest) {
        return filmRequest.getDuration() < 0;
    }

    private boolean isMpaInvalid(FilmRequest filmRequest) {
        if (filmRequest.getMpa() == null) {
            return false;
        }

        boolean isIdValid = mpaRepository.findById(filmRequest.getMpa().getId()).isEmpty();

        return filmRequest.getMpa().getId() < 0 || filmRequest.getMpa().getId() > 6
                || isIdValid;
    }

    private boolean areGenresInvalid(FilmRequest filmRequest) {

        List<Integer> genreIds = genreRepository.getAllGenreIds();

        if (filmRequest.getGenres() == null) return false;

        for (GenreDto genre : filmRequest.getGenres()) {
            Integer genreId = genre.getId();

            if (genreId == null) {
                continue;
            }

            if ((genreId <= 0 || genreId > genreIds.size()) || genreIds.get(genre.getId() - 1) == null) {
                return true;
            }
        }
        return false;
    }
}
