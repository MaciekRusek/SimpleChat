package user;

import auth.PasswordService;

import java.sql.*;
import java.util.Optional;

public class UserDao {
    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:chat.db";

    private Connection conn;
    private Statement stat;

    public UserDao() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            stat = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        createTables();
    }

    public boolean createTables() {
        String createUsers = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR(255) UNIQUE, password VARCHAR(255) )";
        try {
            stat.execute(createUsers);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean checkIfUsernameExists(String username) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=(?);"
            );

            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }


    public Optional<User> insertUser(String username, String password) {
        try {
            String hashPassword = PasswordService.hashPassword(password);
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO users VALUES (NULL, ?, ?);"
            );

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashPassword);
            preparedStatement.execute();

            User user = new User(username);
            return Optional.of(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<User> getUser(String username, String password) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=(?);"
            );

            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();

            String passwordFromDB;
            while (result.next()) {
                passwordFromDB = result.getString("password");

                if (PasswordService.comparePassword(password, passwordFromDB)) {
                    return Optional.of(new User(username));
                } else {
                    return Optional.empty();
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
