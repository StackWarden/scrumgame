package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.RiddleQuestion;

import java.sql.*;
import java.util.*;

public class BenefitsRoomSeeder {

    private static final int ROOM_NUMBER = 1;
    private static final String LEVEL_TYPE = "benefits";

    private static final List<Question> BENEFITS_QUESTIONS = List.of(
            new BaseQuestion(-1, "What is one benefit of using Scrum?", "Improved communication", "Think about teamwork."),
            new BaseQuestion(-1, "How does Scrum encourage adaptability?", "By using short iterations", "Sprints are useful here."),
            new BaseQuestion(-1, "Why is feedback important in Scrum?", "It allows for continuous improvement", "Think about retrospectives."),
            new BaseQuestion(-1, "Who inspects the product increment in Scrum?", "The stakeholders", "They review progress at Sprint Review."),
            new BaseQuestion(-1, "What is one outcome of regular Daily Scrums?", "Better team alignment", "It keeps everyone on the same page.")
    );

    public static void seedBenefitsRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : BENEFITS_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Benefits room successfully seeded for session " + sessionId);

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
        throw new SQLException("Failed to insert level_log for benefits room.");
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