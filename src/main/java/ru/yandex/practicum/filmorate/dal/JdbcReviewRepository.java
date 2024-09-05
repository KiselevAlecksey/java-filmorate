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

@Repository("JdbcReviewRepository")
public class JdbcReviewRepository extends BaseRepository<Review> implements ReviewRepository {

    public JdbcReviewRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    @Override
    public Review save(Review review) {
        String query = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (:content, :is_positive, :user_id, :film_id, :useful)";

        Long id = insert(query, createParameterSource(review));

        if (id != null) {
            review.setReviewId(id);
            return review;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Review updateReview(Review review) {
        String query = "UPDATE reviews SET content = :content, is_positive = :is_positive," +
                " user_id = :user_id, film_id = :film_id, useful = :useful WHERE id = :id";

        update(query, createParameterSource(review));

        Review reviewUpdate =  getById(review.getReviewId()).orElseThrow(
                () -> new InternalServerException("Не удалось сохранить данные"));

        reviewUpdate = getReviewWithRating(
                reviewUpdate,
                getLikeCount(reviewUpdate.getReviewId()),
                getDislikeCount(reviewUpdate.getReviewId()));

        return getReviewWithRating(
                reviewUpdate,
                getLikeCount(reviewUpdate.getReviewId()),
                getDislikeCount(reviewUpdate.getReviewId())
        );

    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM reviews WHERE id = :id";

        delete(query, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Optional<Review> getById(Long id) {
        String query = "SELECT * FROM reviews WHERE id = :id";

        Optional<Review> reviewOptional = findOne(query, new MapSqlParameterSource().addValue("id", id));

        Integer countLike = getLikeCount(id);

        Integer countDislike = getDislikeCount(id);

        Review review = reviewOptional.orElseThrow(
                () -> new NotFoundException("Отзыв не найден"));

        Review reviewWithRating = getReviewWithRating(review, countLike, countDislike);

        if (reviewWithRating != null) return Optional.of(reviewWithRating);

        return reviewOptional;
    }

    private static Review getReviewWithRating(Review review, Integer countLike, Integer countDislike) {

        if (countLike != null && countDislike != null) {
            review.setUseful(countLike - countDislike);
            return review;
        }

        return review;
    }

    private Integer getDislikeCount(Long id) {
        String queryDislikeCount = "SELECT COUNT(*) AS like_count " +
                "FROM reviews_reactions " +
                "WHERE review_id = :review_id AND reaction_dislike = :reaction_dislike";

        Integer countDislike = jdbc.queryForObject(queryDislikeCount,
                new MapSqlParameterSource()
                        .addValue("review_id", id)
                        .addValue("reaction_dislike", true), Integer.class);
        return countDislike;
    }

    private Integer getLikeCount(Long id) {
        String queryLikeCount = "SELECT COUNT(*) AS like_count " +
                "FROM reviews_reactions " +
                "WHERE review_id = :review_id AND reaction_like = :reaction_like";

        Integer countLike = jdbc.queryForObject(queryLikeCount,
                new MapSqlParameterSource()
                        .addValue("review_id", id)
                        .addValue("reaction_like", true), Integer.class);
        return countLike;
    }

    @Override
    public Collection<Review> getReviewsByFilmId(Long filmId, Long count) {
        long countDefault = 10L;

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT id, content, is_positive, user_id, film_id, useful FROM reviews"
        );

        if (filmId != null) {
            queryBuilder.append(" WHERE film_id = :filmId");
        }

        queryBuilder.append(" ORDER BY useful DESC");

        if (count == null) {
            count = countDefault;
        }

        queryBuilder.append(" LIMIT ").append(count);

        String query = queryBuilder.toString();

        Set<Review> reviewSet = new TreeSet<>(Comparator.comparingInt(Review::getUseful).reversed());

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filmId != null) {
            params.addValue("filmId", filmId);
        }

        params.addValue("limit", count);

        List<Review> reviews = new ArrayList<>();

        jdbc.query(query, params, (ResultSet rs, int rowNum) -> {
            Review review = mapper.mapRow(rs, rowNum);
            reviews.add(review);
            return null;
        });

        for (Review review : reviews) {
            Integer countDislike = getDislikeCount(review.getReviewId());
            Integer countLike = getLikeCount(review.getReviewId());

            if (countLike != null && countDislike != null) {
                review.setUseful(countLike - countDislike);
            }

            reviewSet.add(review);
        }

        return reviewSet;
    }

    @Override
    public Collection<Review> getAllReviews(Long count) {
        String query = "SELECT * FROM reviews LIMIT " + count;

        Set<Review> reviewSet = new TreeSet<>(Comparator.comparingInt(Review::getUseful).reversed());

        List<Review> reviews = new ArrayList<>();

        jdbc.query(query,(ResultSet rs, int rowNum) -> {
            Review review = mapper.mapRow(rs, rowNum);
            reviews.add(review);
            return null;
        });

        for (Review review : reviews) {
            Integer countDislike = getDislikeCount(review.getReviewId());
            Integer countLike = getLikeCount(review.getReviewId());

            if (countLike != null && countDislike != null) {
                review.setUseful(countLike - countDislike);
            }

            reviewSet.add(review);
        }

        return reviewSet;
    }

    @Override
    public Collection<Review> getAllReviews() {
        String query = "SELECT * FROM reviews";

        Set<Review> reviewSet = new TreeSet<>(Comparator.comparingInt(Review::getUseful).reversed());

        jdbc.query(query,(ResultSet rs, int rowNum) -> {
            Review review = mapper.mapRow(rs, rowNum);
            reviewSet.add(review);
            return null;
        });

        return reviewSet;
    }

    @Override
    public void addLike(Long id, Long userId) {
        String query = "INSERT INTO reviews_reactions(review_id, user_id, reaction_like) " +
                "VALUES (:review_id, :user_id, :reaction_like)";

        update(query, new MapSqlParameterSource()
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
        String query = "UPDATE reviews_reactions SET reaction_like = :reaction_like WHERE review_id = :review_id AND user_id = :user_id";

        update(query, new MapSqlParameterSource()
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
        String query = "INSERT INTO reviews_reactions(review_id, user_id, reaction_dislike) " +
                "VALUES (:review_id, :user_id, :reaction_dislike)";

        removeLike(id, userId);

        update(query, new MapSqlParameterSource()
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
        String query = "UPDATE reviews_reactions SET reaction_dislike = :reaction_dislike WHERE review_id = :review_id AND user_id = :user_id";

        update(query, new MapSqlParameterSource()
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
}
