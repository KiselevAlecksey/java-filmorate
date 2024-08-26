package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.FilmSql.*;

@Repository("JdbcFilmRepository")

public class JdbcFilmRepository extends BaseRepository implements FilmRepository  {

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Mpa> mpaMapper;

    public JdbcFilmRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);

        genreMapper = new GenreRowMapper(jdbc);

        mpaMapper = new MpaRowMapper(jdbc);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        params.put("user_id", userId);
        update(ADD_LIKE_QUERY, params);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        params.put("user_id", userId);
        update(REMOVE_LIKE_QUERY, params);
    }

    @Override
    public Film save(Film film) {
        Map<String, Object> params = new HashMap<>();

        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", Timestamp.from(film.getReleaseDate())); // Дата в Timestamp
        params.put("duration", film.getDuration());
        params.put("rating_id", film.getMpa().getId());

        Long id = insert(INSERT_FILM, params);

        film.setId(id);

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("film_id", film.getId());
                objectMap.put("genre_id", genre.getId());
                update(INSERT_FILM_GENRE_QUERY, objectMap);
            });
        }

        return film;
    }

    @Override
    public Film update(Film film) {

        Map<String, Object> params = new HashMap<>();

        params.put("id", film.getId());
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", Timestamp.from(film.getReleaseDate()));
        params.put("duration", film.getDuration());
        params.put("rating_id", film.getMpa().getId());

        update(UPDATE_FILM, params);

        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);

        return findOne(FIND_BY_ID_QUERY, params);
    }

    @Override
    public Collection<Film> values() {
        return jdbc.query(FIND_ALL_FILMS, mapper);
    }

    @Override
    public boolean remove(Film film) {
        Map<String, Object> params = new HashMap<>();

        params.put("id", film.getId());

        return delete(DELETE_FILM, params);
    }

    @Override
    public List<Film> getTopPopular() {
        return jdbc.query(GET_POPULAR_FILMS_QUERY, mapper);
    }

    @Override
    public Collection<Film> findFilmsByGenre(Long id) {
        Map<String, Object> params = new HashMap<>();

        params.put("genre_id", id);

        return findMany(FIND_FILM_BY_ID_GENRE, params);
    }

    @Override
    public Optional<Film> findFilm(Long id) {

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);

        Optional<Film> filmOptional = findOne(FIND_FILMS_BY_ID, params);

        Film film = filmOptional.orElseThrow(() -> new NotFoundException("Фильм не найден"));

        Mpa mpa = findMpaById(film.getMpa().getId()).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>(getFilmGenres(film.getId())));

        return filmOptional;
    }

    public Optional<Mpa> findMpaById(Integer id) {
        String query = "SELECT * FROM rating WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        Mpa mpa = jdbc.queryForObject(query, params, mpaMapper);
        return Optional.ofNullable(mpa);
    }

    private Collection<Genre> getFilmGenres(Long filmId) {
        String query = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id AS id FROM film_genres WHERE film_id = :film_id)";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);

        Collection<Genre> genres = jdbc.query(query, params, genreMapper);
        return genres;
    }
}
