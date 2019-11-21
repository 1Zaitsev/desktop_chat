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

    public static void saveHistory(String nick, String message) throws SQLException {
        String request = String.format("INSERT INTO history (nick, message) VAlUES ('%s', '%s')", nick, message);
        statement.execute(request);
    }

    public static StringBuilder getChatHistory() throws SQLException {
        StringBuilder result = new StringBuilder();
        String request = String.format("SElECT nick, message FROM history ORDER BY id");
        ResultSet resultSet = statement.executeQuery(request);

        while (resultSet.next()){
            result.append(resultSet.getString("nick") + " " + resultSet.getString("message") +"\n");
        }
        return result;
    }

//    public static void main(String[] args) {
//        try {
//            connection();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        try {
//            statement.executeUpdate("CREATE TABLE IF EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, nick TEXT, post TEXT)");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
