package ru.yandex.practicum.filmorate.model;


public class UserSql {
    public static final String FIND_ALL_USERS = "SELECT * FROM users";
    public static final String FIND_ALL_FRIENDS = "SELECT * from users " +
            "WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
    public static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?," +
            " name = ?, birthday = ? WHERE id = ?";
    public static final String FIND_BY_ID_QUERY_USERS = "SELECT * FROM users WHERE id = ?";

    public static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    public static final String INSERT_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    public static final String DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    public static final String FIND_BY_ID_FRIENDS_QUERY = "SELECT f2.friend_id \n" +
            "FROM friends f1 \n" +
            "JOIN friends f2 ON f1.user_id = f2.friend_id \n" +
            "WHERE f1.friend_id = ?";
}
