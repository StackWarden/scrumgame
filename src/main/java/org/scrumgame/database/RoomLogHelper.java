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
            PreparedStatement stmt;
            stmt = conn.prepareStatement("SELECT question_id FROM level_log WHERE session_id = ? ORDER BY id DESC LIMIT 1");
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("question_id");
                }
            } catch (SQLException e) {
                System.out.println("error: " + e);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e);
        }
        return null;
    }
}
