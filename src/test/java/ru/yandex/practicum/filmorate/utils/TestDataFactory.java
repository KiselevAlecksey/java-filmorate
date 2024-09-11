package ru.yandex.practicum.filmorate.utils;


import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.LinkedHashSet;

public class TestDataFactory {
    public static final long TEST_USER_ID = 1L;
    public static final long TEST_FILM_ID = 1L;
    public static final long TEST_FILM2_ID = 2L;
    public static final long TEST_USER2_ID = 2L;
    public static final long TEST_FILM3_ID = 3L;
    public static final int TEST_GENRE_ID = 1;
    public static final int TOTAL_GENRES = 6;
    public static final int TEST_MPA_ID = 1;
    public static final int TOTAL_MPA = 5;
    public static final int TEST_LIKE_COUNT = 1;
    public static final int COUNT_ZERO = 0;
    public static final int COUNT_ONE = 1;
    public static final int TEST_INT_YEAR = 2024;
    public static final int TEST_COUNT = 2;
    public static final int TEST_INT_ONE = 1;
    public static final int TEST_INT_TWO = 2;
    public static final long TEST_DIRECTOR_ID = 1L;

    public static final long TEST_REVIEW_ID = 1;
    public static final int TEST_REVIEW_USEFUL = 0;
    public static final int TOTAL_REVIEWS = 1;
    public static final int TEST_DISLIKE_COUNT = -1;

    public static final Instant TEST_RELEASE_DATE = Instant.ofEpochMilli(1_714_608_000_000L);
    public static final Instant TEST_RELEASE_DATE2 = Instant.ofEpochMilli(1_515_468_800_000L);

    public static final Film TEST_FILM = getTestFilm();
    public static final User TEST_USER = getTestUser();
    public static final Mpa TEST_MPA = getMpa();
    public static final LinkedHashSet<Director> TEST_DIRECTORS = getDirectors();
    public static final LinkedHashSet<Genre> TEST_GENRES = getGenres();

    public static Film getTestFilm(Film film) {
        return film.toBuilder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .genres(getGenres())
                .mpa(getMpa())
                .directors(getDirectors())
                .build();
    }

    public static User getTestUser(User user) {
        return user.toBuilder()
                .id(TEST_USER_ID)
                .email(user.getEmail())
                .name(user.getName())
                .login(user.getLogin())
                .birthday(TEST_RELEASE_DATE)
                .build();
    }

    private static User getTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("example@email.ru")
                .name("name")
                .login("login")
                .birthday(TEST_RELEASE_DATE)
                .build();
    }

    private static Film getTestFilm() {
        return Film.builder()
                .id(TEST_FILM_ID)
                .name("name")
                .description("description")
                .releaseDate(TEST_RELEASE_DATE)
                .duration(100)
                .genres(getGenres())
                .mpa(getMpa())
                .directors(getDirectors())
                .build();
    }

    private static LinkedHashSet<Genre> getGenres() {
        Genre genre = new Genre();
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();

        genre.setId(TEST_INT_ONE);
        genre.setName("Комедия");
        genres.add(genre);
        return genres;
    }

    private static Mpa getMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        return mpa;
    }

    private static LinkedHashSet<Director> getDirectors() {
        Director director = new Director();
        LinkedHashSet<Director> directors = new LinkedHashSet<>();
        director.setId(TEST_DIRECTOR_ID);
        director.setName("Имя Режиссера");
        directors.add(director);
        return directors;
    }

}
