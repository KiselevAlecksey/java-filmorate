package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultFilmService implements FilmService {

    @Autowired
    @Qualifier("JdbcFilmRepository")
    private final FilmRepository filmRepository;

    @Autowired
    @Qualifier("JdbcUserRepository")
    private final UserRepository userRepository;

    @Autowired
    @Qualifier("JdbcGenreRepository")
    private final GenreRepository genreRepository;

    @Autowired
    @Qualifier("JdbcMpaRepository")
    private final MpaRepository mpaRepository;

    private final Validator validate;

    @Override
    public FilmDto getFilmWithGenre(Long id) {
        if (id == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        Film film = filmRepository.findFilm(id).orElseThrow(() ->
                new NotFoundException("Фильм с id = " + id + " не найден")
        );

        setGenreAndMpa(id, film, film.getMpa());

        return FilmMapper.mapToFilmDto(film);
    }

    private void setGenreAndMpa(Long id, Film film, Mpa film1) {
        Collection<Genre> genres = genreRepository.getFilmGenres(id);

        film.setGenres(new LinkedHashSet<>(genres));

        Mpa mpa = mpaRepository.findById(film1.getId()).orElseThrow(() ->
                new NotFoundException("MPA с id = " + id + " не найден")
        );

        film.setMpa(mpa);
    }

    @Override
    public FilmDto add(NewFilmRequest filmRequest) {

        validate.validateFilmRequest(filmRequest);

        Film putFilm = FilmMapper.mapToFilm(filmRequest);

        filmRepository.save(putFilm);

        setGenreAndMpa(putFilm.getId(), putFilm, putFilm.getMpa());

        return FilmMapper.mapToFilmDto(putFilm);
    }

    @Override
    public FilmDto update(UpdateFilmRequest filmRequest) {

        if (filmRequest.getId() == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (filmRepository.findById(filmRequest.getId()).isPresent()) {

            validate.validateFilmRequest(filmRequest);

            Film updateFilm = filmRepository.findById(filmRequest.getId())
                    .map(film -> FilmMapper.updateFilmFields(film, filmRequest))
                    .orElseThrow(() -> new NotFoundException("Фильм не найден"));

            filmRepository.update(updateFilm);

            setGenreAndMpa(updateFilm.getId(), updateFilm, updateFilm.getMpa());

            return FilmMapper.mapToFilmDto(updateFilm);
        }
        throw new NotFoundException("Фильм с id = " + filmRequest.getId() + " не найден");
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {

        if (filmId == null || userId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        boolean isDb = userRepository.findById(userId).isPresent() & filmRepository.findById(filmId).isPresent();

        if (isDb) {
            filmRepository.addLike(filmId, userId);
            return true;
        }

        throw new NotFoundException(
                "Ресурс с id = "
                        + (userRepository.findById(userId).isEmpty() ? userId : filmId) + " не найден"
        );
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (userRepository.findById(userId).isPresent() & filmRepository.findById(filmId).isPresent()) {
            filmRepository.removeLike(filmId, userId);
            return true;
        }

        throw new NotFoundException(
                "Ресурс с id = "
                        + (userRepository.findById(userId).isEmpty() ? userId : filmId) + " не найден"
        );
    }

    @Override
    public Collection<FilmDto> getPopularFilms(Integer count) {

        int popularFilmsSize = filmRepository.getTopPopular().size();

        int start = 0;

        int end = Math.min(popularFilmsSize, 10);

        if (count == null) {
            return getTopTen(start, end);
        }

        if (count <= 0) {
            throw new ParameterNotValidException("" + count, "Должен быть > 0");
        }

        return filmRepository.getTopPopular().subList(start, count < popularFilmsSize ? count : popularFilmsSize)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList()
                );
    }

    private Collection<FilmDto> getTopTen(int start, int end) {

        return filmRepository.getTopPopular().subList(start, end)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public Collection<FilmDto> findAll() {

        return filmRepository.values().stream()
                .peek(film -> setGenreAndMpa(film.getId(), film, film.getMpa()))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FilmDto> get(Film film) {
        return Optional.ofNullable(filmRepository.findById(film.getId())
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден")));
    }
}
