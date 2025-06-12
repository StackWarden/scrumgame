package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.questions.BaseQuestion;

import java.sql.*;
import java.util.*;

public class DailyScrumRoomSeeder {

    private static final int ROOM_NUMBER = 2;
    private static final String LEVEL_TYPE = "daily_scrum";

    private static final List<Question> DAILY_SCRUM_QUESTIONS = List.of(
            new BaseQuestion(-1, "Wat is het doel van de Daily Scrum?", "Synchronisatie en planning", "Denk aan samenwerking."),
            new BaseQuestion(-1, "Wie neemt deel aan de Daily Scrum?", "Het ontwikkelteam", "Wie werkt dagelijks samen?"),
            new BaseQuestion(-1, "Wat is de maximale duur van een Daily Scrum?", "15 minuten", "Het is kort."),
            new BaseQuestion(-1, "Wanneer vindt de Daily Scrum plaats?", "Elke werkdag", "Denk aan frequentie."),
            new BaseQuestion(-1, "Wat bespreekt men tijdens de Daily Scrum?", "Voortgang en obstakels", "Wat helpt het team vooruit?")
    );

    public static void seedDailyScrumRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : DAILY_SCRUM_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Daily Scrum room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getOrInsertQuestion(Connection conn, Question q) throws SQLException {
        String select = "SELECT id FROM question WHERE text = ?";
        try (PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setString(1, q.getQuestion());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }

        String insert = "INSERT INTO question (text, correct_answer, hint) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, q.getQuestion());
            stmt.setString(2, q.getAnswer());
            stmt.setString(3, q.getHint());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }

        throw new SQLException("Failed to insert question: " + q.getQuestion());
    }

    private static int createLevelLog(Connection conn, int sessionId, int roomNumber, String type) throws SQLException {
        String insert = "INSERT INTO level_log (session_id, room_number, level_type, completed) VALUES (?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, roomNumber);
            stmt.setString(3, type);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }

        throw new SQLException("Failed to insert level_log for Daily Scrum room.");
    }

    private static void insertQuestionLog(Connection conn, int sessionId, int levelLogId, int questionId) throws SQLException {
        String insert = "INSERT INTO question_log (session_id, level_log_id, question_id, completed) VALUES (?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, levelLogId);
            stmt.setInt(3, questionId);
            stmt.executeUpdate();
        }
    }
}
