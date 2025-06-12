package org.scrumgame.database;

import org.scrumgame.database.models.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomLogHelper {
    public static Integer getCurrentRoomNumber(Session session) {
        if (session == null) return null;

        int id = session.getId();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS total_rows FROM level_log WHERE session_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total_rows");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return null;
    }

    public static int getLevelLogIdByRoomNumber(int sessionId, int roomNumber) {
        String query = "SELECT id FROM level_log WHERE session_id = ? AND room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return -1;
    }
}
