package org.scrumgame.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Objects;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.load();
    private static boolean isTestMode = false;

    public static void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    public static void initializeIfNeeded() {
        String dbName = isTestMode ? dotenv.get("DB_NAME_TEST") : dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");

        String adminUrl = String.format("jdbc:mysql://%s:%s/", host, port);

        try (Connection adminConn = DriverManager.getConnection(adminUrl, user, password);
             Statement adminStmt = adminConn.createStatement()) {

            adminStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);

        } catch (SQLException e) {
            System.err.println("Fout bij aanmaken database: " + e.getMessage());
            return;
        }

        try (Connection conn = getConnection()) {

            if (!tableExists(conn, "Role")) {
                System.out.println("Tabellen nog niet aanwezig — initialiseren...");
                runSqlScript(conn, "");
            } else {
                System.out.println("Tabellen al aanwezig");
            }

        } catch (Exception e) {
            System.err.println("Fout bij database-initialisatie: " + e.getMessage());
            e.printStackTrace();
        }
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

    private static void runSqlScript(Connection conn, String resourcePath) throws Exception {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(Objects.requireNonNull(DatabaseConnection.class.getResourceAsStream(resourcePath)))
                );
                Statement stmt = conn.createStatement()
        ) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append(" ");
                if (line.trim().endsWith(";")) {
                    stmt.execute(sb.toString());
                    sb.setLength(0);
                }
            }
        }
    }

    private static boolean tableExists(Connection conn, String tableName) {
        try (PreparedStatement stmt = conn.prepareStatement("SHOW TABLES LIKE ?")) {
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static int countTables(Connection conn) {
        int count = 0;
        try (ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.err.println("Fout bij tellen van tabellen: " + e.getMessage());
        }
        return count;
    }
}