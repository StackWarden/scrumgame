package org.scrumgame.database.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.scrumgame.classes.Achievements;
import org.scrumgame.database.DatabaseConnection;

public class AchievementList {

    public static List<Achievements> getAllAchievements() {
        List<Achievements> achievements = new ArrayList<>();
        String query = "SELECT * FROM achievements";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Achievements achievement = new Achievements(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("unlocked")
                );
                achievements.add(achievement);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger in production
        }

        return achievements;
    }
}
    