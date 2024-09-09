package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.slqreuest.ReviewSql.*;

@Repository("JdbcReviewRepository")
public class JdbcReviewRepository extends BaseRepository<Review> implements ReviewRepository {

    private static final long DEFAULT_COUNT = 10L;

    public JdbcReviewRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    @Override
    public Review save(Review review) {

        Long id = insert(INSERT_REVIEW, createParameterSource(review));

        if (id != null) {
            review.setReviewId(id);
            return review;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Review updateReview(Review review) {

        update(UPDATE_REVIEW, createParameterSource(review));

        Review reviewUpdate = getById(review.getReviewId()).orElseThrow(
                () -> new InternalServerException("Не удалось сохранить данные"));

        reviewUpdate.setUseful(getReaction(review.getReviewId()));
        if (review.getUseful() == null) {
            review.setUseful(0);
        }
        return reviewUpdate;
    }

    @Override
    public void remove(Long id) {
        delete(DELETE_REVIEW, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Optional<Review> getById(Long id) {

        Optional<Review> reviewOptional = findOne(SELECT_REVIEW, new MapSqlParameterSource().addValue("id", id));

        Review review = reviewOptional.orElseThrow(
                () -> new NotFoundException("Отзыв не найден"));

        review.setUseful(getReaction(review.getReviewId()));

        if (review.getUseful() == null) {
            review.setUseful(0);
        }

        return Optional.of(review);
    }

    @Override
    public Collection<Review> getReviewsByFilmId(Long filmId, Long count) {

        String query = buildReviewsByFilmIdQuery(filmId, count);

        Set<Review> reviewSet = new TreeSet<>(Comparator.comparingInt(Review::getUseful).reversed());

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filmId != null) {
            params.addValue("filmId", filmId);
        }

        params.addValue("limit", count);

        reviewSet.addAll(getReviewsFromDb(query, params));

        return reviewSet;
    }

    @Override
    public void addLike(Long id, Long userId) {

        update(INSERT_LIKE, new MapSqlParameterSource()
                .addValue("review_id", id)
                .addValue("user_id", userId)
                .addValue("reaction_like", true)
        );

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

        updateReview(getById(id).orElseThrow(
                () -> new NotFoundException("Отзыв не найден")
        ));
    }

    @Override
    public void addDislike(Long id, Long userId) {
        String query = "SELECT reaction_like FROM reviews_reactions WHERE user_id = :user_id AND reaction_like = TRUE";

        List<Boolean> likeOptional = jdbc.query(query,
                new MapSqlParameterSource().addValue("user_id", userId),
                (ResultSet rs, int rowNum) -> {
                    return rs.getBoolean("reaction_like");
                });

        if (!likeOptional.isEmpty() && likeOptional.getFirst().describeConstable().isPresent()) {
            removeLike(id, userId);
        }

        update(INSERT_DISLIKE, new MapSqlParameterSource()
                .addValue("review_id", id)
                .addValue("user_id", userId)
                .addValue("reaction_dislike", true)
        );

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

    private Map<Long, Integer> getReactions(List<Long> reviewIds) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("review_id", reviewIds);

        return jdbc.query(SELECT_REACTIONS, params, resultSet -> {
            Map<Long, Integer> reactionsMap = new java.util.HashMap<>();
            while (resultSet.next()) {
                Long reviewId = resultSet.getLong("review_id");
                Integer netReaction = resultSet.getInt("net_reaction");
                reactionsMap.put(reviewId, netReaction);
            }
            return reactionsMap;
        });
    }

    private String buildReviewsByFilmIdQuery(Long filmId, Long count) {

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT id, content, is_positive, user_id, film_id, useful FROM reviews"
        );

        if (filmId != null) {
            queryBuilder.append(" WHERE film_id = :filmId");
        }

        queryBuilder.append(" ORDER BY useful DESC");

        queryBuilder.append(" LIMIT ").append(count != null ? count : DEFAULT_COUNT);

        return queryBuilder.toString();
    }

    private Collection<Review> getReviewsFromDb(String query, MapSqlParameterSource params) {

        List<Review> reviews = select(query, params).stream().toList();

        List<Long> ids = reviews.stream()
                .map(Review::getReviewId)
                .toList();

        reviews.forEach(review -> {
            review.setUseful(getReactions(ids).get(review.getReviewId()));

            if (review.getUseful() == null) {
                review.setUseful(0);
            }
        });

        return reviews;
    }
}