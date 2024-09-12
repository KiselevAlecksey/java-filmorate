package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.interfaces.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.mapper.FeedRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utils.FeedUtils;

import java.sql.ResultSet;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.feed.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.feed.enums.Operation.*;
import static ru.yandex.practicum.filmorate.model.slqreuest.ReviewSql.*;

@Repository("JdbcReviewRepository")
public class JdbcReviewRepository extends BaseRepository<Review> implements ReviewRepository {

    private boolean isReviewUpdate = true;

    private static final long DEFAULT_COUNT = 10L;

    private final FeedUtils<Feed> feedUtils;

    public JdbcReviewRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);

        RowMapper<Feed> feedMapper = new FeedRowMapper(jdbc);

        feedUtils = new FeedUtils<>(jdbc, feedMapper);
    }

    @Override
    public Review save(Review review) {

        setUseful(review);

        Long id = insert(INSERT_REVIEW, createParameterSource(review));

        if (id != null) {

            review.setReviewId(id);

            FeedEvent feedEvent = new FeedEvent(review.getUserId(), id, REVIEW.name(), ADD.name());

            feedUtils.saveFeedEvent(feedEvent);

            return review;

        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Review updateReview(Review review) {

        setUseful(review);

        update(UPDATE_REVIEW, createParameterSource(review));

        Review reviewUpdate = getById(review.getReviewId()).orElseThrow(
                () -> new InternalServerException("Не удалось сохранить данные"));

        reviewUpdate.setUseful(getReaction(review.getReviewId()));

        if (isReviewUpdate) {
            FeedEvent feedEvent = new FeedEvent(review.getUserId(),
                    reviewUpdate.getReviewId(), REVIEW.name(), UPDATE.name());

            feedUtils.saveFeedEvent(feedEvent);
        }

        isReviewUpdate = true;

        return reviewUpdate;
    }

    @Override
    public void remove(Long id) {

        Long userId = getById(id).orElseThrow(
                () -> new NotFoundException("id не найден")).getUserId();

        Review review = getById(userId).orElseThrow(
                () -> new NotFoundException("Отзыв не найден")
        );

        delete(DELETE_REVIEW, new MapSqlParameterSource().addValue("id", id));

        FeedEvent feedEvent = new FeedEvent(userId, review.getReviewId(), REVIEW.name(), REMOVE.name());

        feedUtils.saveFeedEvent(feedEvent);
    }

    @Override
    public Optional<Review> getById(Long id) {

        Optional<Review> reviewOptional = findOne(SELECT_REVIEW, new MapSqlParameterSource().addValue("id", id));

        Review review = reviewOptional.orElseThrow(
                () -> new NotFoundException("Отзыв не найден"));

        review.setUseful(getReaction(review.getReviewId()));

        return Optional.of(review);
    }

    @Override
    public Collection<Review> getReviewsByFilmId(Long filmId, Integer count) {

        String query = buildReviewsByFilmIdQuery(filmId, count);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filmId != null) {
            params.addValue("film_id", filmId);
        }

        return getReviewsFromDb(query, params);
    }

    @Override
    public void addLike(Long id, Long userId) {

        update(INSERT_LIKE, new MapSqlParameterSource()
                .addValue("review_id", id)
                .addValue("user_id", userId)
                .addValue("reaction_like", true)
        );

        isReviewUpdate = false;

        updateReview(getById(id).orElseThrow(
                () -> new NotFoundException("Отзыв не найден")
        ));
    }

    @Override
    public void removeLike(Long id, Long userId) {

        update(REMOVE_LIKE, new MapSqlParameterSource()
                .addValue("review_id", id)
                .addValue("user_id", userId)
                .addValue("reaction_like", false)
        );

        isReviewUpdate = false;

        updateReview(getById(id).orElseThrow(
                () -> new NotFoundException("Отзыв не найден")
        ));
    }

    @Override
    public void addDislike(Long id, Long userId) {

        clearLikeAfterDislike(id, userId);

        update(INSERT_DISLIKE, new MapSqlParameterSource()
                .addValue("review_id", id)
                .addValue("user_id", userId)
                .addValue("reaction_dislike", true)
        );

        isReviewUpdate = false;

        updateReview(getById(id).orElseThrow(
                () -> new NotFoundException("Отзыв не найден")
        ));
    }

    @Override
    public void removeDislike(Long id, Long userId) {

        update(REMOVE_DISLIKE, new MapSqlParameterSource()
                .addValue("review_id", id)
                .addValue("user_id", userId)
                .addValue("reaction_dislike", false)
        );

        isReviewUpdate = false;

        updateReview(getById(id).orElseThrow(
                () -> new NotFoundException("Отзыв не найден")
        ));
    }

    private static MapSqlParameterSource createParameterSource(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", review.getReviewId());
        params.addValue("content", review.getContent());
        params.addValue("is_positive", review.getIsPositive());
        params.addValue("user_id", review.getUserId());
        params.addValue("film_id", review.getFilmId());
        params.addValue("useful", review.getUseful());

        return params;
    }

    private Integer getReaction(Long reviewId) {

        return jdbc.queryForObject(SELECT_REACTION,
                new MapSqlParameterSource()
                        .addValue("review_id", reviewId), Integer.class);
    }

    private String buildReviewsByFilmIdQuery(Long filmId, Integer count) {

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT id, content, is_positive, user_id, film_id, useful FROM reviews"
        );

        if (filmId != null) {
            queryBuilder.append(" WHERE film_id = :film_id");
        }

        queryBuilder.append(" ORDER BY useful DESC");

        queryBuilder.append(" LIMIT ").append(count != null ? count : DEFAULT_COUNT);

        return queryBuilder.toString();
    }

    private Collection<Review> getReviewsFromDb(String query, MapSqlParameterSource params) {

        return select(query, params).stream().toList();
    }

    private static void setUseful(Review review) {
        if (review.getUseful() == null) {
            review.setUseful(0);
        }
    }

    private void clearLikeAfterDislike(Long id, Long userId) {
        List<Boolean> likeOptional = jdbc.query(GET_LIKE_REACTION_BY_USER_ID,
                new MapSqlParameterSource().addValue("user_id", userId),
                (ResultSet rs, int rowNum) -> {
                    return rs.getBoolean("reaction_like");
                });

        if (!likeOptional.isEmpty() && likeOptional.getFirst().describeConstable().isPresent()) {
            removeLike(id, userId);
        }
    }
}
