package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository  {

    private final RowMapper<Director> directorMapper;

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Mpa> mpaMapper;

    public JdbcFilmRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);

        directorMapper = new DirectorRowMapper(jdbc);

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

        jdbc.update(UPDATE_FILM, createParameterSource(film));

        List<Long> genreIds = film.getGenres().stream()
                .map(genre -> Long.valueOf(genre.getId()))
                .toList();

        List<Long> directorIds = film.getDirectors().stream()
                .map(Director::getId)
                .toList();

        deleteGenres(film.getId(), genreIds);

        insertGenres(film.getId(), genreIds);

        deleteDirectors(film.getId(), directorIds);

        insertDirectors(film.getId(), directorIds);

        Film updated = getByIdFullDetails(film.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + film.getId() + " не найден"));

        System.out.println(film);

        return getByIdFullDetails(film.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + film.getId() + " не найден"));
    }

    private void insertDirectors(Long filmId, List<Long> directorIds) {
        String insertDirectors = "INSERT INTO film_directors (director_id, film_id) VALUES (:director_id, :film_id)";

        List<MapSqlParameterSource> paramsList = getMapSqlDirectors(filmId, directorIds);

        jdbc.batchUpdate(insertDirectors, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    private void deleteDirectors(Long filmId, List<Long> directorIds) {
        String deleteDirectors = "DELETE FROM film_directors WHERE director_id = :director_id AND film_id = :film_id";

        List<MapSqlParameterSource> paramsList = getMapSqlDirectors(filmId, directorIds);

        jdbc.batchUpdate(deleteDirectors, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    private void insertGenres(Long filmId, List<Long> genreIds) {

        String insertGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id)";

        List<MapSqlParameterSource> paramsList = getMapSqlGenres(filmId, genreIds);

        jdbc.batchUpdate(insertGenres, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    private void deleteGenres(Long filmId, List<Long> genreIds) {

        String deleteGenres = "DELETE FROM film_genres WHERE film_id = :film_id AND genre_id = :genre_id";

        List<MapSqlParameterSource> paramsList = getMapSqlGenres(filmId, genreIds);

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
    public boolean remove(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
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

        Mpa mpa = getMpaById(film.getMpa().getId()).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>(getFilmGenres(film.getId())));

        film.setDirectors(new LinkedHashSet<>(getFilmDirectors(film.getId())));

        return Optional.of(film);
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
                mapper
        );

        List<Film> recommendedFilmsWithFullDetails = new ArrayList<>();
        for (Film film : recommendedFilms) {
            Optional<Film> fullDetailsFilm = getByIdFullDetails(film.getId());
            fullDetailsFilm.ifPresent(recommendedFilmsWithFullDetails::add);
        }

        return recommendedFilmsWithFullDetails;
    }

    public Optional<Mpa> getMpaById(Integer id) {
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

    private Collection<Director> getFilmDirectors(Long filmId) {
        String query = "SELECT * FROM directors " +
                "WHERE id IN (SELECT director_id AS id FROM film_directors WHERE film_id = :film_id)";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);

        Collection<Director> directors = jdbc.query(query, params, directorMapper);
        return directors;
    }


    private static List<MapSqlParameterSource> getMapSqlGenres(Long filmId, List<Long> genreIds) {
        List<MapSqlParameterSource> paramsList = new ArrayList<>();

        for (Long genreId : genreIds) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("film_id", filmId);
            params.addValue("genre_id", genreId);
            paramsList.add(params);
        }
        return paramsList;
    }

    private static List<MapSqlParameterSource> getMapSqlDirectors(Long filmId, List<Long> directorIds) {
        List<MapSqlParameterSource> paramsList = new ArrayList<>();

        for (Long id : directorIds) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("director_id", id);
            params.addValue("film_id", filmId);
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

    public List<Film> getFilmsByDirector(Long dirId, List<String> sort) {

        String query = "SELECT * FROM films WHERE id IN " +
                "(SELECT film_id AS id FROM film_directors WHERE director_id = :director_id)";

        List<Film> filmSort = new ArrayList<>();

        if (sort.getFirst().equals("year")) {

            filmSort = jdbc.query(query, new MapSqlParameterSource().addValue("director_id", dirId), mapper);

        } else if (sort.getLast().equals("likes")) {

            filmSort = jdbc.query(
                    "SELECT f.*, COUNT(l.user_id) AS like_count " +
                            "FROM film_directors AS fd " +
                            "LEFT JOIN films AS f ON fd.film_id = f.id " +
                            "LEFT JOIN likes AS l ON f.id = l.film_id " +
                            "WHERE fd.director_id = :director_id " +
                            "GROUP BY f.id " +
                            "ORDER BY like_count DESC",
                    new MapSqlParameterSource().addValue("director_id", dirId), mapper
            );

        }

        return filmSort;
    }
}
