package org.scrumgame.observers.achievements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.scrumgame.interfaces.Achievements.MonsterObserver;

public class MonsterAchievementObserver implements MonsterObserver {
    @Override
    public void onFiveMonstersDefeated() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:scrumgame.db")) {
            String sql = "UPDATE achievements SET unlocked = 1 WHERE naam = 'Monster killer'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Achievement 'Monster killer' unlocked!");
    }
}