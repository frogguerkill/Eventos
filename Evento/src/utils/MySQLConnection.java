package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    private static Connection connection;

    public static Connection connect(
            String host,
            String database,
            String user,
            String password
    ) {

        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            String url = "jdbc:mysql://" + host + "/" + database +
                    "?useSSL=false&autoReconnect=true&characterEncoding=utf8";

            connection = DriverManager.getConnection(url, user, password);
            return connection;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
