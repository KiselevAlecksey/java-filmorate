package ru.yandex.practicum.filmorate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Constant;
import ru.yandex.practicum.filmorate.model.Genre;

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

        boolean isValid = filmRequest.getMpa().getId() < 0 || filmRequest.getMpa().getId() > 6
                || isIdValid;
        return isValid;
    }

    private boolean areGenresInvalid(FilmRequest filmRequest) {

        List<Integer> genreIds = genreRepository.getAllGenreIds();

        if (filmRequest.getGenres() == null) return false;

        for (Genre genre : filmRequest.getGenres()) {
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
