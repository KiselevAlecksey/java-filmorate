package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.FilmSql.*;
import static ru.yandex.practicum.filmorate.model.slqreuest.GenreSql.ALL_GENRE_QUERY;
import static ru.yandex.practicum.filmorate.model.slqreuest.MpaSql.ALL_MPA_QUERY;

@Repository("JdbcFilmRepository")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Mpa> mpaMapper;

    private final RowMapper<Film> filmMapper;

    public JdbcFilmRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);

        genreMapper = new GenreRowMapper(jdbc);

        mpaMapper = new MpaRowMapper(jdbc);

        filmMapper = new FilmRowMapper(jdbc, genreMapper, mpaMapper);

    }

    @Override
    public void addLike(Long filmId, Long userId) {
        update(ADD_LIKE_QUERY, new MapSqlParameterSource().addValue("film_id", filmId).addValue("user_id", userId));
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        update(REMOVE_LIKE_QUERY, new MapSqlParameterSource().addValue("film_id", filmId).addValue("user_id", userId));
    }

    @Override
    public Film save(Film film) {

        Long id = insert(INSERT_FILM, createParameterSource(film));

        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            List<Long> genreIds = film.getGenres().stream()
                    .map(genre -> Long.valueOf(genre.getId()))
                    .toList();

            insertGenres(id, genreIds);
        }

        return getByIdFullDetails(film.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + film.getId() + " не найден"));
    }

    @Override
    public Film update(Film film) {

        update(UPDATE_FILM, createParameterSource(film));

        List<Long> genreIds = film.getGenres().stream()
                .map(genre -> Long.valueOf(genre.getId()))
                .toList();

        deleteGenres(film.getId(), genreIds);

        insertGenres(film.getId(), genreIds);

        return getByIdFullDetails(film.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + film.getId() + " не найден"));
    }

    private void insertGenres(Long filmId, List<Long> genreIds) {

        String insertGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id)";

        List<MapSqlParameterSource> paramsList = getMapSqlParameterSources(filmId, genreIds);

        jdbc.batchUpdate(insertGenres, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    private void deleteGenres(Long filmId, List<Long> genreIds) {

        String deleteGenres = "DELETE FROM film_genres WHERE film_id = :film_id AND genre_id = :genre_id";

        List<MapSqlParameterSource> paramsList = getMapSqlParameterSources(filmId, genreIds);

        jdbc.batchUpdate(deleteGenres, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    @Override
    public Optional<Film> getByIdPartialDetails(Long id) {

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);

        return findOne(FIND_BY_ID_QUERY, params);
    }

    @Override
    public Collection<Film> values() {

        List<Film> films = jdbc.query(ALL_FILMS_QUERY, mapper);

        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getFilmGenresMap();

        Map<Long, Mpa> filmMpaMap = getMpaMap();

        setFilmDetails(films, filmGenresMap, filmMpaMap);

        return films;
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
    public Optional<Film> getByIdFullDetails(Long id) {

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);

        Optional<Film> filmOptional = findOne(FIND_FILMS_BY_ID, params);

        Film film = filmOptional.orElseThrow(() -> new NotFoundException("Фильм не найден"));

        Mpa mpa = findMpaById(film.getMpa().getId()).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>(getFilmGenres(film.getId())));

        return filmOptional;
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(Optional<Integer> countOpt, Integer genreId, Integer year) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT f.*, COUNT(l.user_id) AS likes_count ")
                .append("FROM films f ")
                .append("LEFT JOIN likes l ON f.id = l.film_id ");

        if (genreId != null) {
            queryBuilder.append("JOIN film_genres fg ON f.id = fg.film_id ");
        }

        queryBuilder.append("WHERE 1=1 ");

        if (genreId != null) {
            queryBuilder.append("AND fg.genre_id = :genre_id ");
        }

        if (year != null) {
            queryBuilder.append("AND YEAR(f.release_date) = :year ");
        }

        queryBuilder.append("GROUP BY f.id ")
                .append("ORDER BY likes_count DESC ");

        if (countOpt.isPresent() && countOpt.get() > 0) {
            queryBuilder.append("LIMIT :count");
        }

        String query = queryBuilder.toString();

        Map<String, Object> params = new HashMap<>();
        if (countOpt.isPresent() && countOpt.get() > 0) {
            params.put("count", countOpt.get());
        }
        if (genreId != null) {
            params.put("genre_id", genreId);
        }
        if (year != null) {
            params.put("year", year);
        }

        return jdbc.query(query, params, mapper);
    }

    @Override
    public List<Film> getRecommendedFilms(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);

        List<Film> recommendedFilms = jdbc.query(
                GET_RECOMMENDED_FILMS,
                params,
                filmMapper
        );

        List<Film> recommendedFilmsWithFullDetails = new ArrayList<>();
        for (Film film : recommendedFilms) {
            Optional<Film> fullDetailsFilm = getByIdFullDetails(film.getId());
            fullDetailsFilm.ifPresent(recommendedFilmsWithFullDetails::add);
        }

        return recommendedFilmsWithFullDetails;
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


    private static List<MapSqlParameterSource> getMapSqlParameterSources(Long filmId, List<Long> genreIds) {
        List<MapSqlParameterSource> paramsList = new ArrayList<>();

        for (Long genreId : genreIds) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("film_id", filmId);
            params.addValue("genre_id", genreId);
            paramsList.add(params);
        }
        return paramsList;
    }

    private static MapSqlParameterSource createParameterSource(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", Timestamp.from(film.getReleaseDate()));
        params.addValue("duration", film.getDuration());
        params.addValue("rating_id", film.getMpa().getId());

        return params;
    }

    private static void setFilmDetails(List<Film> films, Map<Long,
            LinkedHashSet<Genre>> filmGenresMap, Map<Long, Mpa> filmMpaMap) {

        for (Film film : films) {
            LinkedHashSet<Genre> genres = filmGenresMap.get(film.getId());
            if (genres != null) {
                film.setGenres(genres);
            }

            Mpa mpa = filmMpaMap.get(film.getId());

            if (mpa != null) {
                film.setMpa(mpa);
            }
        }
    }

    private Map<Long, Mpa> getMpaMap() {
        Map<Long, Mpa> filmMpaMap = new HashMap<>();

        jdbc.query(ALL_MPA_QUERY, (ResultSet rs, int rowNum) -> {
            Long filmId = rs.getLong("film_id");
            Mpa mpa = mpaMapper.mapRow(rs,rowNum);

            filmMpaMap.put(filmId, mpa);
            return null;
        });
        return filmMpaMap;
    }

    private Map<Long, LinkedHashSet<Genre>> getFilmGenresMap() {
        Map<Long, LinkedHashSet<Genre>> filmGenresMap = new HashMap<>();

        jdbc.query(ALL_GENRE_QUERY, (ResultSet rs, int rowNum) -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = genreMapper.mapRow(rs, rowNum);

            filmGenresMap.computeIfAbsent(filmId, key -> new LinkedHashSet<>()).add(genre);
            return null;
        });
        return filmGenresMap;
    }
}
