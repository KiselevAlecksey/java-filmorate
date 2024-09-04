package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
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
            review.setId(id);
            return review;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Review update(Review review) {
        String query = "UPDATE reviews SET review = :review, is_positive = :is_positive," +
                " useful = :useful, film_id = :film_id, user_id = :user_id WHERE id = :id";

        update(query, createParameterSource(review));

        return getById(review.getId()).orElseThrow(
                () -> new InternalServerException("Не удалось сохранить данные"));
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM reviews WHERE id = :id";

        delete(query, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Optional<Review> getById(Long id) {
        String query = "SELECT * FROM reviews WHERE id = :id";

        Optional<Review> review = Optional.ofNullable(jdbc.queryForObject(query, new MapSqlParameterSource().addValue("id", id), mapper));
        return review;
    }

    @Override
    public Collection<Review> getReviewsByFilmId(Long filmId, Long count) {
        long countDefault = 10L;

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT id, review, is_positive, useful, film_id, user_id FROM reviews"
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

        jdbc.query(query, params, (ResultSet rs, int rowNum) -> {
            Review review = mapper.mapRow(rs, rowNum);
            reviewSet.add(review);
            return null;
        });

        return reviewSet;
    }

    @Override
    public void addLike(Long id, Long userId) {

    }

    @Override
    public void removeLike(Long id, Long userId) {

    }

    @Override
    public void addDislike(Long id, Long userId) {

    }

    @Override
    public void removeDislike(Long id, Long userId) {

    }

    private static MapSqlParameterSource createParameterSource(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", review.getId());
        params.addValue("content", review.getContent());
        params.addValue("is_positive", review.isPositive());
        params.addValue("user_id", review.getUserId());
        params.addValue("film_id", review.getFilmId());
        params.addValue("useful", review.getUseful());

        return params;
    }
}
