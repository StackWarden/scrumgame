package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseSeeder {
    private final int sessionId;

    public BaseSeeder(int sessionId) {
        this.sessionId = sessionId;
    }

    public void seedGame() {
        BenefitsRoomSeeder benefitsRoomSeeder = new BenefitsRoomSeeder(sessionId);
        DailyScrumRoomSeeder dailyScrumRoomSeeder = new DailyScrumRoomSeeder(sessionId);
        PlanningRoomSeeder planningRoomSeeder = new PlanningRoomSeeder(sessionId);
        RetrospectiveRoomSeeder retrospectiveRoomSeeder = new RetrospectiveRoomSeeder(sessionId);
        ScrumBoardRoomSeeder scrumBoardRoomSeeder = new ScrumBoardRoomSeeder(sessionId);
        SprintReviewRoomSeeder sprintReviewRoomSeeder = new SprintReviewRoomSeeder(sessionId);

        benefitsRoomSeeder.seed(sessionId);
        dailyScrumRoomSeeder.seed(sessionId);
        planningRoomSeeder.seed(sessionId);
        retrospectiveRoomSeeder.seed(sessionId);
        scrumBoardRoomSeeder.seed(sessionId);
        sprintReviewRoomSeeder.seed(sessionId);
    }

    public int getOrInsertQuestion(Connection conn, Question q) throws SQLException {
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

    public int createLevelLog(Connection conn, int sessionId, int roomNumber, String type) throws SQLException {
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
        throw new SQLException("Failed to insert level_log for benefits room.");
    }

    public void insertQuestionLog(Connection conn, int sessionId, int levelLogId, int questionId) throws SQLException {
        String insert = "INSERT INTO question_log (session_id, level_log_id, question_id, completed) VALUES (?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, levelLogId);
            stmt.setInt(3, questionId);
            stmt.executeUpdate();
        }
    }

    public void insertDataToDatabase(int sessionId, int roomNumber, String levelType, List<Question> questions) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : questions) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, roomNumber, levelType);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Benefits room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
