package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class SprintReviewRoomSeeder {

    private static final int ROOM_NUMBER = 6;
    private static final String LEVEL_TYPE = "sprintreview";

    private static final List<Question> SPRINT_REVIEW_QUESTIONS = List.of(
            new Question(-1, "What is the main goal of the Sprint Review?", "To inspect the Increment and adapt the Product Backlog", "It's about reviewing work and planning future work."),
            new Question(-1, "Who attends the Sprint Review?", "The Scrum Team and stakeholders", "Think about collaboration and feedback."),
            new Question(-1, "What is demonstrated during the Sprint Review?", "The Increment", "It’s the usable product result."),
            new Question(-1, "When does the Sprint Review take place?", "At the end of the Sprint", "It's just before the Retrospective."),
            new Question(-1, "What is a possible outcome of the Sprint Review?", "An updated Product Backlog", "It helps determine the next priorities.")
    );

    public static void seedSprintReviewRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : SPRINT_REVIEW_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("SprintReview room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getOrInsertQuestion(Connection conn, Question q) throws SQLException {
        String select = "SELECT id FROM question WHERE text = ?";
        try (PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setString(1, q.getQuestion());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insert = "INSERT INTO question (text, correct_answer, hint) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, q.getQuestion());
            stmt.setString(2, q.getAnswer());
            stmt.setString(3, q.getHint());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
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
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to insert level_log for sprint review room.");
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
