package org.scrumgame.observers.achievements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.scrumgame.interfaces.Achievements.GameCompletedObserver;

public class GameCompletedAchievementObserver implements GameCompletedObserver {
    @Override
    public void onGameCompleted() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:scrumgame.db")) {
            String sql = "UPDATE achievements SET unlocked = 1 WHERE naam = 'Game Completed'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Achievement 'Game Completed' unlocked!");
    }
}