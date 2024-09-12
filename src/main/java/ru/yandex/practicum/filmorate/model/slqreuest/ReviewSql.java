package ru.yandex.practicum.filmorate.model.slqreuest;

public class ReviewSql {

    public static final String INSERT_REVIEW = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "VALUES (:content, :is_positive, :user_id, :film_id, :useful)";

    public static final String UPDATE_REVIEW = "UPDATE reviews SET content = :content, " +
            "is_positive = :is_positive, user_id = :user_id, film_id = :film_id, useful = :useful WHERE id = :id";

    public static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = :id";

    public static final String SELECT_REVIEW = "SELECT * FROM reviews WHERE id = :id";

    public static final String INSERT_LIKE = "INSERT INTO reviews_reactions(review_id, user_id, reaction_like) " +
            "VALUES (:review_id, :user_id, :reaction_like)";

    public static final String REMOVE_LIKE = "UPDATE reviews_reactions " +
            "SET reaction_like = :reaction_like " +
            "WHERE review_id = :review_id AND user_id = :user_id";

    public static final String INSERT_DISLIKE = "INSERT INTO reviews_reactions(review_id, user_id, reaction_dislike) " +
            "VALUES (:review_id, :user_id, :reaction_dislike)";

    public static final String REMOVE_DISLIKE = "UPDATE reviews_reactions " +
            "SET reaction_dislike = :reaction_dislike " +
            "WHERE review_id = :review_id AND user_id = :user_id";

    public static final String SELECT_REACTION = "SELECT " +
            "COALESCE(SUM(CASE WHEN reaction_like = TRUE THEN 1 ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN reaction_dislike = TRUE THEN 1 ELSE 0 END), 0) AS net_reaction " +
            "FROM reviews_reactions " +
            "WHERE review_id = :review_id";

    public static final String SELECT_REACTIONS = "SELECT " +
            "review_id, " +
            "COALESCE(SUM(CASE WHEN reaction_like = TRUE THEN 1 ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN reaction_dislike = TRUE THEN 1 ELSE 0 END), 0) AS net_reaction " +
            "FROM reviews_reactions " +
            "WHERE review_id IN (:review_id) " +
            "GROUP BY review_id";

    public static final String GET_LIKE_REACTION_BY_USER_ID = "\n" +
            "SELECT reaction_like FROM reviews_reactions \n" +
            "WHERE user_id = :user_id AND reaction_like = TRUE";
}
