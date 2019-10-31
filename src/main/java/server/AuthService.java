package server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement statement;

    public static void connection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:target/classes/usersDB");
        statement = connection.createStatement();
    }

    public static void disconnection() throws SQLException{
            connection.close();
    }

    public static String getNickByLoginAndPassword(String login, String password) throws SQLException {
        String request = String.format("SELECT nickname FROM users WHERE login ='%s' AND password = '%s';", login, password);
        ResultSet resultSet = statement.executeQuery(request);
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }
}
