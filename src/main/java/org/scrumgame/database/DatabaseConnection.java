package org.scrumgame.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.load();
    private static boolean isTestMode = false;

    public static void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    public static Connection getConnection() throws SQLException {
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");
        String dbName = isTestMode ? dotenv.get("DB_NAME_TEST") : dotenv.get("DB_NAME");

        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, dbName);
        return DriverManager.getConnection(url, user, password);
    }
}