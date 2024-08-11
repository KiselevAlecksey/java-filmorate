package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Constant;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DefaultFilmService implements FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    long currentMaxId = 0;

    @Override
    public boolean addLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId) != null & filmRepository.findById(filmId) != null) {
            filmRepository.addLike(filmId, userId);
            return true;
        }

        throw new NotFoundException(
                "Ресурс с id = "
                        + (userRepository.findById(userId) == null ? userId : filmId) + " не найден"
        );
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId) != null & filmRepository.findById(filmId) != null) {
            filmRepository.removeLike(filmId, userId);
            return true;
        }

        throw new NotFoundException(
                "Ресурс с id = "
                        + (userRepository.findById(userId) == null ? userId : filmId) + " не найден"
        );
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {

        int popularFilmsSize = filmRepository.getPopularFilms().size();

        int start = 0;

        int end = Math.min(popularFilmsSize, 10);

        if (count == null) {
            return filmRepository.getPopularFilms().subList(start, end);
        }

        if (count <= 0) {
            throw new ParameterNotValidException("" + count, "Должен быть > 0");
        }

        return filmRepository.getPopularFilms().subList(start, count < popularFilmsSize ? count : popularFilmsSize);
    }

    @Override
    public Collection<Film> findAll() {
        return filmRepository.values();
    }

    @Override
    public Film get(Film film) {
        return filmRepository.findById(film.getId());
    }

    @Override
    public Film add(Film film) {

        if (film.getName().isBlank()
                || film.getDescription().length() > Constant.MAX_LENGTH_DESCRIPTION
                || film.getReleaseDate().isBefore(Instant.ofEpochMilli(Constant.CINEMA_BURN_DAY))
                || film.getDuration() < 0) {

            throw new ConditionsNotMetException(
                    "Название не может быть пустым," +
                    " описание не может быть больше " + Constant.MAX_LENGTH_DESCRIPTION +
                    " дата релиза не может быть раньше " + Constant.CINEMA_BURN_DAY +
                    " продолжительность не может быть отрицательной"
            );
        }

        Film putFilm = Film.builder()
                .id(getNextId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();

        filmRepository.save(putFilm);
        return putFilm;
    }

    @Override
    public Film update(Film film) {

        if (film.getId() == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (filmRepository.findById(film.getId()) != null) {

            if (film.getName().isBlank()
                    || film.getDescription().length() > Constant.MAX_LENGTH_DESCRIPTION
                    || film.getReleaseDate().isBefore(Instant.ofEpochMilli(Constant.CINEMA_BURN_DAY))
                    || film.getDuration() < 0) {

                throw new ConditionsNotMetException(
                        "Название не может быть пустым," +
                                " описание не может быть больше " + Constant.MAX_LENGTH_DESCRIPTION +
                                " дата релиза не может быть раньше " + Constant.CINEMA_BURN_DAY +
                                " продолжительность не может быть отрицательной"
                );
            }

            Film updateFilm = film.toBuilder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .build();

            filmRepository.save(film);

            return updateFilm;
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
