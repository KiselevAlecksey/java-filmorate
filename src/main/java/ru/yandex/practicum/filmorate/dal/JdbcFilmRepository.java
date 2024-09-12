package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.*;
import ru.yandex.practicum.filmorate.dal.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.utils.FeedUtils;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.feed.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.feed.enums.Operation.*;
import static ru.yandex.practicum.filmorate.model.slqreuest.DirectorSql.ALL_DIRECTOR_QUERY;
import static ru.yandex.practicum.filmorate.model.slqreuest.FilmSql.*;
import static ru.yandex.practicum.filmorate.model.slqreuest.GenreSql.ALL_GENRE_QUERY;
import static ru.yandex.practicum.filmorate.model.slqreuest.MpaSql.ALL_MPA_QUERY;

@Slf4j
@Repository("JdbcFilmRepository")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {

    private final RowMapper<Director> directorMapper;

    private final RowMapper<Genre> genreMapper;

    private final RowMapper<Mpa> mpaMapper;

    private final RowMapper<Feed> feedMapper;

    private final FeedUtils<Feed> feedUtils;

    public JdbcFilmRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);

        directorMapper = new DirectorRowMapper(jdbc);

        genreMapper = new GenreRowMapper(jdbc);

        mpaMapper = new MpaRowMapper(jdbc);

        feedMapper = new FeedRowMapper(jdbc);

        feedUtils = new FeedUtils<>(jdbc, feedMapper);
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        Integer count = getLikeCount(filmId, userId);

        FeedEvent feedEvent = new FeedEvent(userId, filmId, LIKE.name(), ADD.name());

        feedUtils.saveFeedEvent(feedEvent);

        if (count != null && count > 0) {
            return;
        }

        update(ADD_LIKE_QUERY, new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId));
    }

    private Integer getLikeCount(Long filmId, Long userId) {

        Integer count = jdbc.queryForObject(USER_LIKE_COUNT_IN_LIKES, new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId),
                Integer.class
        );
        return count;
    }

    @Override
    public void removeLike(Long filmId, Long userId) {

        update(REMOVE_LIKE_QUERY, new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId));

        FeedEvent feedEvent = new FeedEvent(userId, filmId, LIKE.name(), REMOVE.name());

        feedUtils.saveFeedEvent(feedEvent);
    }

    @Override
    public Film save(Film film) {

        Long id = insert(INSERT_FILM, createParameterSource(film));

        film.setId(id);

        saveComplexFields(film, id);

        return getByIdFullDetails(film.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + film.getId() + " не найден"));
    }

    private void saveComplexFields(Film film, Long id) {

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            List<Long> genreIds = film.getGenres().stream()
                    .map(genre -> Long.valueOf(genre.getId()))
                    .toList();

            insertGenres(id, genreIds);
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {

            List<Long> directorIds = film.getDirectors().stream()
                    .map(Director::getId)
                    .toList();

            insertDirectors(id, directorIds);
        }
    }

    @Override
    public Film update(Film film) {

        jdbc.update(UPDATE_FILM, createParameterSource(film));

        updateComplexFields(film);

        Film filmUpdated = getByIdFullDetails(film.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + film.getId() + " не найден"));

        return filmUpdated;
    }

    private void updateComplexFields(Film film) {

        List<Long> genreIds = film.getGenres().stream()
                .map(genre -> Long.valueOf(genre.getId()))
                .toList();

        List<Long> directorIds = film.getDirectors().stream()
                .map(Director::getId)
                .toList();

        deleteGenres(film.getId());

        insertGenres(film.getId(), genreIds);

        deleteDirectors(film.getId());

        insertDirectors(film.getId(), directorIds);
    }

    private void insertDirectors(Long filmId, List<Long> directorIds) {

        List<MapSqlParameterSource> paramsList = getMapSqlDirectors(filmId, directorIds);

        jdbc.batchUpdate(INSERT_DIRECTORS, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    private void deleteDirectors(Long filmId) {
        delete(DELETE_DIRECTORS, new MapSqlParameterSource().addValue("film_id", filmId));
    }

    private void insertGenres(Long filmId, List<Long> genreIds) {

        List<MapSqlParameterSource> paramsList = getMapSqlGenres(filmId, genreIds);

        jdbc.batchUpdate(INSERT_GENRES, paramsList.toArray(new MapSqlParameterSource[0]));
    }

    private void deleteGenres(Long filmId) {
        delete(DELETE_GENRES, new MapSqlParameterSource().addValue("film_id", filmId));
    }

    @Override
    public Optional<Film> getByIdPartialDetails(Long id) {
        return findOne(FIND_BY_ID_QUERY, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Collection<Film> values() {

        List<Film> films = jdbc.query(ALL_FILMS_QUERY, mapper);

        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getFilmGenresMap();

        Map<Long, Mpa> filmMpaMap = getMpaMap();

        Map<Long, LinkedHashSet<Director>> filmDirectorsMap = getFilmDirectorsMap();

        setFilmDetails(films, filmGenresMap, filmMpaMap, filmDirectorsMap);

        return films;
    }

    @Override
    public boolean remove(Long id) {
        return delete(DELETE_FILM, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public List<Film> getTopPopular() {

        List<Film> films = jdbc.query(GET_POPULAR_FILMS_QUERY, mapper);

        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getFilmGenresMap();

        Map<Long, Mpa> filmMpaMap = getMpaMap();

        Map<Long, LinkedHashSet<Director>> filmDirectorsMap = getFilmDirectorsMap();

        setFilmDetails(films, filmGenresMap, filmMpaMap, filmDirectorsMap);

        return films;
    }

    @Override
    public Collection<Film> findFilmsByGenre(Long id) {
        return findMany(FIND_FILM_BY_ID_GENRE, new MapSqlParameterSource().addValue("genre_id", id));
    }

    @Override
    public Optional<Film> getByIdFullDetails(Long id) {

        Optional<Film> filmOptional = findOne(FIND_FILMS_BY_ID, new MapSqlParameterSource().addValue("id", id));

        Film film = filmOptional.orElseThrow(() -> new NotFoundException("Фильм не найден"));

        Mpa mpa = findMpaById(film.getMpa().getId()).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>(getFilmGenres(film.getId(), film.getGenres().stream()
                .map(genre -> Long.valueOf(genre.getId()))
                .toList())));

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

        List<Film> films = jdbc.query(query, params, mapper);

        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getFilmGenresMap();

        Map<Long, Mpa> filmMpaMap = getMpaMap();

        Map<Long, LinkedHashSet<Director>> filmDirectorsMap = getFilmDirectorsMap();

        setFilmDetails(films, filmGenresMap, filmMpaMap, filmDirectorsMap);

        return films;
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

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);
        Collection<Film> commonFilms = jdbc.query(
                GET_COMMON_FILMS,
                params,
                mapper
        );

        if (commonFilms.isEmpty()) {
            log.info("No common films found for user ID: {} and friend ID: {}", userId, friendId);
        } else {
            log.info("Found {} common films for user ID: {} and friend ID: {}", commonFilms.size(), userId, friendId);
            commonFilms.forEach(film -> log.info("Film ID: {}, Name: {}", film.getId(), film.getName()));
        }
        List<Film> commonFilmsWithFullDetails = new ArrayList<>();
        for (Film film : commonFilms) {
            Optional<Film> fullDetailsFilm = getByIdFullDetails(film.getId());
            fullDetailsFilm.ifPresent(commonFilmsWithFullDetails::add);
        }

        return commonFilmsWithFullDetails;
    }

    public Optional<Mpa> findMpaById(Integer id) {
        String query = "SELECT * FROM rating WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        Mpa mpa = jdbc.queryForObject(query, params, mpaMapper);
        return Optional.ofNullable(mpa);
    }

    private Collection<Genre> getFilmGenres(Long filmId, List<Long> genreIds) {
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

    private Collection<Director> getDirector(Long id) {
        String query = "SELECT * FROM directors " +
                "WHERE id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

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

    private static void setFilmDetails(
            List<Film> films, Map<Long,
            LinkedHashSet<Genre>> filmGenresMap,
            Map<Long, Mpa> filmMpaMap, Map<Long,
            LinkedHashSet<Director>> filmDirectorsMap) {

        for (Film film : films) {
            LinkedHashSet<Genre> genres = filmGenresMap.get(film.getId());
            if (genres != null) {
                film.setGenres(genres);
            }

            Mpa mpa = filmMpaMap.get(film.getId());

            if (mpa != null) {
                film.setMpa(mpa);
            }

            LinkedHashSet<Director> directors = filmDirectorsMap.get(film.getId());

            if (directors != null) {
                film.setDirectors(directors);
            }
        }
    }

    private Map<Long, Mpa> getMpaMap() {
        Map<Long, Mpa> filmMpaMap = new HashMap<>();

        jdbc.query(ALL_MPA_QUERY, (ResultSet rs, int rowNum) -> {
            Long filmId = rs.getLong("film_id");
            Mpa mpa = mpaMapper.mapRow(rs, rowNum);

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

    private Map<Long, LinkedHashSet<Director>> getFilmDirectorsMap() {
        Map<Long, LinkedHashSet<Director>> filmDirectorsMap = new HashMap<>();

        jdbc.query(ALL_DIRECTOR_QUERY, (ResultSet rs, int rowNum) -> {
            Long filmId = rs.getLong("film_id");
            Director director = directorMapper.mapRow(rs, rowNum);

            filmDirectorsMap.computeIfAbsent(filmId, key -> new LinkedHashSet<>()).add(director);
            return null;
        });
        return filmDirectorsMap;
    }

    public List<Film> getFilmsByDirector(Long dirId, List<String> sort) {

        String queryReleaseDate = "\n" +
                "   SELECT f.* \n" +
                "   FROM film_directors AS fd \n" +
                "   LEFT JOIN films AS f ON fd.film_id = f.id \n" +
                "   WHERE fd.director_id = :director_id \n" +
                "   GROUP BY f.id \n" +
                "   ORDER BY release_date";

        List<Film> filmSort = new ArrayList<>();

        if (sort.getFirst().equals("year")) {

            filmSort = jdbc.query(queryReleaseDate, new MapSqlParameterSource().addValue("director_id", dirId), mapper);

        } else if (sort.getLast().equals("likes")) {

            if (getDirector(dirId).isEmpty()) {
                throw new NotFoundException("Режиссёр не найден");
            }

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

    @Override
    public List<Film> search(String query, String[] searchFields) {
        if (query == null || query.trim().isEmpty() || searchFields == null || searchFields.length == 0) {
            return Collections.emptyList();
        }
        query = query.trim().toLowerCase();

        String fieldsParam;
        if (searchFields.length == 2) {
            fieldsParam = " LOWER(TRIM(f.name)) " + "LIKE '%" + query +
                    "%'" + " OR " + " LOWER(TRIM(d.name)) LIKE '%" + query + "%'";
        } else {
            if (searchFields[0].equals("title")) {
                fieldsParam = "LOWER(TRIM(f.name)) " + "LIKE '%" + query + "%'";
            } else {
                fieldsParam = "LOWER(TRIM(d.name))" + " LIKE '%" + query + "%'";
            }
        }
        String searchQuery = SEARCH_QUERY + " WHERE " +
                fieldsParam
                + " GROUP BY f.id " +
                "ORDER BY likes_count DESC";

        List<Film> films = jdbc.query(searchQuery, mapper);

        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getFilmGenresMap();

        Map<Long, Mpa> filmMpaMap = getMpaMap();

        Map<Long, LinkedHashSet<Director>> filmDirectorsMap = getFilmDirectorsMap();

        setFilmDetails(films, filmGenresMap, filmMpaMap, filmDirectorsMap);

        LinkedHashSet<Film> filmsSet = new LinkedHashSet<>(films);

        return filmsSet.stream().toList();
    }
}
