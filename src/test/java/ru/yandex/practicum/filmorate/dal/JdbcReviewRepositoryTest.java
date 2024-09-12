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
import ru.yandex.practicum.filmorate.dal.interfaces.FilmRepository;
import ru.yandex.practicum.filmorate.dal.interfaces.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.interfaces.UserRepository;
import ru.yandex.practicum.filmorate.dal.mapper.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.utils.TestDataFactory.*;


@JdbcTest
@Import({JdbcFilmRepository.class, JdbcReviewRepository.class, JdbcUserRepository.class, JdbcDirectorRepositoryTest.class,
        DirectorRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class,
        UserRowMapper.class, ReviewRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcReviewRepository")
public class JdbcReviewRepositoryTest {

    private final ReviewRepository reviewRepository;

    private final FilmRepository filmRepository;

    private final UserRepository userRepository;

    private final NamedParameterJdbcTemplate jdbc;

    @BeforeEach
    void init() {

        jdbc.update("DELETE FROM films", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        jdbc.update("DELETE FROM users", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        jdbc.update("DELETE FROM reviews", new MapSqlParameterSource());
        jdbc.update("ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1",  new MapSqlParameterSource());

        userRepository.save(TEST_USER);
        filmRepository.save(TEST_FILM);
        filmRepository.save(getTestFilm(TEST_FILM));

        reviewRepository.save(TEST_REVIEW);
    }

    @Test
    @DisplayName("должен добавлять отзыв")
    void should_save_review() {

        Review reviewOptional = reviewRepository.save(TEST_REVIEW);

        assertThat(reviewOptional)
                .usingRecursiveComparison()
                .isEqualTo(TEST_REVIEW);
    }

    @Test
    @DisplayName("должен обновлять отзыв")
    void should_update_review() {

        TEST_REVIEW.setContent("Обновленный отзыв");

        reviewRepository.addLike(TEST_REVIEW_ID, TEST_USER_ID);

        reviewRepository.updateReview(TEST_REVIEW);

        Optional<Review> reviewOptional = reviewRepository.getById(TEST_REVIEW_ID);

        assertThat(reviewOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(TEST_REVIEW);
    }

    @Test
    @DisplayName("должен удалять отзыв")
    void should_remove_review() {

        reviewRepository.remove(TEST_REVIEW.getReviewId());

        assertThrows(NotFoundException.class, () -> reviewRepository.getById(TEST_REVIEW_ID));
    }


    @Test
    @DisplayName("должен возвращать отзывы по id фильма с заданным количеством count")
    void should_return_reviews_by_film_id_with_count() {

        Collection<Review> reviews = reviewRepository.getReviewsByFilmId(TEST_FILM_ID, COUNT_ONE);

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

        assertThat(review.getUseful()).isEqualTo(COUNT_ZERO);
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

        assertThat(review.getUseful()).isEqualTo(COUNT_ZERO);
    }
}