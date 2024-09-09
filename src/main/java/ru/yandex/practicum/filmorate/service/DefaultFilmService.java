package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;
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

    @Autowired
    @Qualifier("JdbcDirectorRepository")
    private final DirectorRepository directorRepository;

    private final Validator validator;

    @Override
    public FilmDto getById(Long id) {

        if (id == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        Film film = filmRepository.getByIdFullDetails(id).orElseThrow(() ->
                new NotFoundException("Фильм с id = " + id + " не найден")
        );

        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto add(NewFilmRequest filmRequest) {

        validator.validateFilmRequest(filmRequest);

        Film putFilm = FilmMapper.mapToFilm(filmRequest);

        validateGenre(filmRequest);

        validateMpa(filmRequest);

        putFilm = filmRepository.save(putFilm);

        return FilmMapper.mapToFilmDto(putFilm);
    }

    public boolean remove(Long id) {
        if (id == null || filmRepository.getByIdPartialDetails(id).isEmpty()) {
            throw new NotFoundException("Id не найден");
        }
        return filmRepository.remove(id);
    }

    @Override
    public FilmDto update(UpdateFilmRequest filmRequest) {

        validator.validateFilmRequest(filmRequest);

        Film updatedFilm = filmRepository.getByIdPartialDetails(filmRequest.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        validateGenre(filmRequest);

        validateMpa(filmRequest);

        validateDirectors(filmRequest);

        FilmMapper.updateFilmFields(updatedFilm, filmRequest);

        filmRepository.update(updatedFilm);

        return FilmMapper.mapToFilmDto(updatedFilm);

    }

    @Override
    public boolean addLike(Long filmId, Long userId) {

        if (filmId == null || userId == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        boolean isDb = userRepository.findById(userId).isPresent()
                & filmRepository.getByIdPartialDetails(filmId).isPresent();

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

        if (userRepository.findById(userId).isPresent()
                & filmRepository.getByIdPartialDetails(filmId).isPresent()) {
            filmRepository.removeLike(filmId, userId);
            return true;
        }

        throw new NotFoundException(
                "Ресурс с id = "
                        + (userRepository.findById(userId).isEmpty() ? userId : filmId) + " не найден"
        );
    }

    @Override
    public Collection<FilmDto> getPopularFilms(Optional<Integer> countOpt) {

        int popularFilmsSize = filmRepository.getTopPopular().size();

        int start = 0;

        int end = Math.min(popularFilmsSize, 10);

        int count = countOpt.orElse(0);

        if (countOpt.isEmpty()) {
            return getTopTen(0, Math.min(10, popularFilmsSize));
        }

        if (count <= 0) {
            throw new ParameterNotValidException("" + count, "Должен быть > 0");
        }

        return filmRepository.getTopPopular().subList(start, Math.min(count, popularFilmsSize))
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public Collection<FilmDto> getPopularFilmsByGenresAndYears(Optional<Integer> countOpt, Integer genreId, Integer year) {
        List<Film> films = filmRepository.getPopularFilmsByGenreAndYear(countOpt, genreId, year);
        int limit = countOpt.orElse(Integer.MAX_VALUE);
        return films.stream()
                .limit(limit)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
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
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    public Optional<FilmDto> getById(Film film) {
        return Optional.ofNullable(filmRepository.getByIdFullDetails(film.getId())
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден")));
    }

    @Override
    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        return filmRepository.values().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateGenre(FilmRequest filmRequest) {
        if (filmRequest.getGenres() != null) {
            List<Integer> genreIds = filmRequest.getGenres().stream().map(Genre::getId).toList();

            List<Genre> genres = genreRepository.getByIds(genreIds);

            if (genreIds.size() != genres.size()) {
                throw new NotFoundException("Жанры не найдены");
            }

            filmRequest.setGenres(new LinkedHashSet<>(genres));
        }
    }

    private void validateDirectors(FilmRequest filmRequest) {
        if (filmRequest.getDirectors() != null) {
            List<Long> directorIds = filmRequest.getDirectors().stream().map(Director::getId).toList();

            List<Director> directors = directorRepository.getByIds(directorIds);

            if (directorIds.size() != directors.size()) {
                throw new NotFoundException("Жанры не найдены");
            }

            filmRequest.setDirectors(new LinkedHashSet<>(directors));
        }
    }

    private void validateMpa(FilmRequest filmRequest) {
        if (filmRequest.getMpa().getId() != null) {
            Mpa mpa = filmRequest.getMpa();

            mpa = mpaRepository.findById(mpa.getId()).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

            filmRequest.setMpa(mpa);
        }
    }

    private void setGenreAndMpa(Long id, Film film, Mpa film1) {
        Collection<Genre> genres = genreRepository.getFilmGenres(id);

        film.setGenres(new LinkedHashSet<>(genres));

        Mpa mpa = mpaRepository.findById(film1.getId()).orElseThrow(() ->
                new NotFoundException("MPA с id = " + id + " не найден")
        );

        film.setMpa(mpa);
    }


    public List<Film> getFilmsByDirector(Long dirId, List<String> sort) {
        List<Film> films = filmRepository.getFilmsByDirector(dirId, sort);

        return films.stream().map(film -> filmRepository.getByIdFullDetails(film.getId())
                        .orElseThrow(() -> new NotFoundException("Фильм не найден")))
                .toList();
    }
}
