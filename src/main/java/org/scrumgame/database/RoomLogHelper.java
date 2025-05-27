package org.scrumgame.database;

import org.scrumgame.database.models.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomLogHelper {
    public static Integer getCurrentRoomNumber(Session session) {
        if (session == null) return null;

        int logId = session.getCurrentRoomId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt;

            if (logId > 0) {
                stmt = conn.prepareStatement(
                        "SELECT question_id FROM level_log WHERE id = ?");
                stmt.setInt(1, logId);
            } else {
                stmt = conn.prepareStatement(
                        "SELECT question_id FROM level_log WHERE session_id = ? AND completed = true ORDER BY id DESC LIMIT 1");
                stmt.setInt(1, session.getId());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("question_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
