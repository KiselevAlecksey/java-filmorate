package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Timestamp;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.FilmSql.*;

@Repository("JdbcFilmRepository")
public class JdbcFilmRepository extends BaseRepository implements FilmRepository  {

    public JdbcFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public Film save(Film film) {
        Long id = insert(
                INSERT_FILM,
                film.getName(),
                film.getDescription(),
                Timestamp.from(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );

        film.setId(id);
        if (film.getGenres() != null) {
            saveGenres(film.getId(), new ArrayList<>(film.getGenres()));
        }
        return film;
    }

    @Override
    public Film update(Film film) {

        update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                Timestamp.from(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getId()
        );
        LinkedHashSet<Genre> updatedGenres = updateGenres(film.getId(), new ArrayList<>(film.getGenres()));
        film.setGenres(updatedGenres);
        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Film> values() {
        return findMany(FIND_ALL_FILMS);
    }

    @Override
    public boolean remove(Film film) {
        return delete(DELETE_QUERY, film.getId());
    }

    @Override
    public List<Film> getPopularFilms() {
        return findMany(GET_POPULAR_FILMS_QUERY, 10);
    }

    @Override
    public Collection<Film> findFilmsByGenre(Long id) {

        return findMany(FIND_FILM_BY_ID_GENRE, id);
    }

    @Override
    public Optional<Film> findFilm(Long id) {

        return findOne(FIND_FILMS_BY_ID, id);
    }

    private LinkedHashSet<Genre> updateGenres(Long id, List<Genre> genreList) {
        delete(DELETE_GENRE, id);
        genreList.forEach(genre -> update(
                UPDATE_GENRE,
                id,
                genre.getId()
        ));
        return new LinkedHashSet<>(genreList);
    }

    private List<Genre> saveGenres(Long id, List<Genre> genreList) {

        genreList.forEach(genre -> update(
                INSERT_FILM_GENRE_QUERY,
                id,
                genre.getId()
        ));
        return genreList;
    }
}
