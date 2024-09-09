package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mapper.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@JdbcTest
@Import({JdbcFilmRepository.class, JdbcReviewRepository.class, JdbcUserRepository.class, JdbcDirectorRepository.class,
        DirectorRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class,
        UserRowMapper.class, ReviewRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcReviewRepository")
public class JdbcReviewRepositoryTest {

    public static final long TEST_DIRECTOR_ID = 1L;
    public static final long TEST_USER_ID = 1L;
    public static final int TEST_GENRE_ID = 1;
    public static final long TEST_REVIEW_ID = 1L;
    public static final long TEST_FILM_ID = 1L;
    public static final int TEST_REVIEW_USEFUL = 0;
    public static final int TOTAL_REVIEWS = 1;
    public static final int TEST_LIKE_COUNT = 1;
    public static final int TEST_DISLIKE_COUNT = -1;
    public static final int ZERO = 0;

    private final JdbcReviewRepository reviewRepository;

    private final JdbcFilmRepository filmRepository;

    private final JdbcUserRepository userRepository;

    private final NamedParameterJdbcTemplate jdbc;

    private Review review;

    @BeforeEach
    void init() {

        jdbc.update("DELETE FROM films", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        jdbc.update("DELETE FROM users", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        jdbc.update("DELETE FROM reviews", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        userRepository.save(getTestUser());
        filmRepository.save(getTestFilm());

        review = reviewRepository.save(getReview());
    }

    @Test
    @DisplayName("должен добавлять отзыв")
    void should_save_review() {

        Optional<Review> reviewOptional = reviewRepository.getById(TEST_REVIEW_ID);

        assertThat(reviewOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getReview());
    }

    @Test
    @DisplayName("должен обновлять отзыв")
    void should_update_review() {

        review.setContent("Обновленный отзыв");
        reviewRepository.updateReview(review);

        Optional<Review> reviewOptional = reviewRepository.getById(TEST_REVIEW_ID);

        assertThat(reviewOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(review);
    }

    @Test
    @DisplayName("должен удалять отзыв")
    void should_remove_review() {

        reviewRepository.remove(getReview().getReviewId());

        assertThrows(NotFoundException.class, () -> reviewRepository.getById(TEST_REVIEW_ID));
    }


    @Test
    @DisplayName("должен возвращать отзывы по id фильма с заданным количеством count")
    void should_return_reviews_by_film_id_with_count() {

        Collection<Review> reviews = reviewRepository.getReviewsByFilmId(TEST_FILM_ID, TEST_REVIEW_ID);

        assertThat(reviews)
                .isNotEmpty()
                .hasSize(TOTAL_REVIEWS)
                .extracting(Review::getReviewId)
                .contains(TEST_REVIEW_ID);
    }

    @Test
    @DisplayName("должен добавлять лайк")
    void should_addLike_review() {
        reviewRepository.addLike(TEST_REVIEW_ID, TEST_USER_ID);

        Review review = reviewRepository.getById(TEST_REVIEW_ID).orElseThrow();

        assertThat(review.getUseful()).isEqualTo(TEST_LIKE_COUNT);
    }

    @Test
    @DisplayName("должен удалять лайк")
    void should_removeLike_review() {
        reviewRepository.addLike(TEST_REVIEW_ID, TEST_USER_ID);
        reviewRepository.removeLike(TEST_REVIEW_ID, TEST_USER_ID);

        Review review = reviewRepository.getById(TEST_REVIEW_ID).orElseThrow();

        assertThat(review.getUseful()).isEqualTo(ZERO);
    }

    @Test
    @DisplayName("должен добавлять дизлайк")
    void should_addDislike_review() {

        reviewRepository.addDislike(TEST_REVIEW_ID, TEST_USER_ID);

        Review review = reviewRepository.getById(TEST_REVIEW_ID).orElseThrow();

        assertThat(review.getUseful()).isEqualTo(TEST_DISLIKE_COUNT);
    }

    @Test
    @DisplayName("должен удалять дизлайк")
    void should_removeDislike_review() {

        reviewRepository.addDislike(TEST_REVIEW_ID, TEST_USER_ID);

        reviewRepository.removeDislike(TEST_REVIEW_ID, TEST_USER_ID);

        Review review = reviewRepository.getById(TEST_REVIEW_ID).orElseThrow();

        assertThat(review.getUseful()).isEqualTo(ZERO);
    }

    private static Review getReview() {
        Review review = new Review();
        review.setReviewId(TEST_REVIEW_ID);
        review.setContent("Отзыв");
        review.setIsPositive(true);
        review.setUserId(TEST_USER_ID);
        review.setFilmId(TEST_FILM_ID);
        review.setUseful(TEST_REVIEW_USEFUL);

        return review;
    }

    private static User getTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("example@email.ru")
                .name("name")
                .login("description")
                .birthday(Instant.ofEpochMilli(1_714_608_000_000L))
                .build();
    }

    private static LinkedHashSet<Genre> getGenres() {
        Genre genre = new Genre();
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genre.setId(TEST_GENRE_ID);
        genres.add(genre);
        return genres;
    }

    private static LinkedHashSet<Director> getDirectors() {
        Director director = new Director();
        LinkedHashSet<Director> directors = new LinkedHashSet<>();
        director.setId(TEST_DIRECTOR_ID);
        director.setName("Имя Режиссера");
        directors.add(director);
        return directors;
    }

    private static Film getTestFilm() {
        return Film.builder()
                .id(TEST_FILM_ID)
                .name("name")
                .description("description")
                .releaseDate(Instant.ofEpochMilli(1_714_608_000_000L))
                .duration(100)
                .genres(getGenres())
                .mpa(getMpa())
                .directors(getDirectors())
                .build();
    }

    private static Mpa getMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        return mpa;
    }
}