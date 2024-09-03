package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Review review = new Review();
        review.setId(resultSet.getLong("id"));
        review.setReview(resultSet.getString("review"));
        review.setPositive(resultSet.getBoolean("is_positive"));
        review.setUseful(resultSet.getInt("useful"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUserId(resultSet.getLong("user_id"));

        return review;
    }
}
