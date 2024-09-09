package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("ReviewRowMapper")
@RequiredArgsConstructor
public class ReviewRowMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Review review = new Review();
        review.setReviewId(resultSet.getLong("id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUseful(resultSet.getInt("useful"));

        return review;
    }
}
