package org.scrumgame.observers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.scrumgame.interfaces.HintlessObserver;

public class HintlessGameCompletion implements HintlessObserver {
    @Override
    public void onHintlessBeat() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:scrumgame.db")) {
            String sql = "UPDATE achievements SET unlocked = 1 WHERE naam = 'Hintless'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Achievement 'Hintless' unlocked!");

    }
}
