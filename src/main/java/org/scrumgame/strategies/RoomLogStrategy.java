package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Question;
import org.scrumgame.commands.Game;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.GameLog;
import org.scrumgame.interfaces.LogStrategy;
import org.scrumgame.rooms.Benefits;
import org.scrumgame.interfaces.RoomLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomLogStrategy implements LogStrategy {

    @Override
    public Level log(Session session, Level level) {
        // Room-levels worden vooraf gezaaid, dus hier geen log nodig
        return null;
    }

    @Override
    public List<? extends GameLog> getLogs(Session session) {
        return new ArrayList<>();
    }

    @Override
    public void markCurrentLogCompleted(Session session) {
        String sql = "UPDATE level_log SET completed = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getCurrentRoomId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public String getPromptByLogId(int logId) {
        RoomLevel level = (RoomLevel) loadByLogId(logId);
        return level != null ? level.getPrompt() : "Geen prompt gevonden.";
    }

    @Override
    public Level loadByLogId(int logId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            RoomMetadata metadata = fetchRoomMetadata(conn, logId);
            if (metadata == null) return null;

            List<QuestionLog> questionLogs = fetchQuestionLogs(conn, logId);

            // Mark level as completed if no questions remain
            if (questionLogs.isEmpty()) {
                markLevelAsCompleted(conn, logId);
            }

            RoomLevel level = createRoomLevel(metadata.levelType, questionLogs);
            if (level == null) return null;

            level.setLogId(logId);
            level.setRoomNumber(metadata.roomNumber);

            return (Level) level;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    private record RoomMetadata(int roomNumber, String levelType, boolean completed) {}

    private RoomMetadata fetchRoomMetadata(Connection conn, int logId) throws SQLException {
        String sql = "SELECT room_number, level_type, completed FROM level_log WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new RoomMetadata(
                        rs.getInt("room_number"),
                        rs.getString("level_type"),
                        rs.getBoolean("completed")
                );
            }
        }
        return null;
    }

    private List<QuestionLog> fetchQuestionLogs(Connection conn, int levelLogId) throws SQLException {
        String sql = """
        SELECT ql.id AS log_id, ql.session_id, ql.completed, ql.level_log_id,
               q.id AS question_id, q.text, q.correct_answer, q.hint
        FROM question_log ql
        JOIN question q ON ql.question_id = q.id
        WHERE ql.level_log_id = ?
        ORDER BY ql.id ASC
    """;

        List<QuestionLog> logs = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, levelLogId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int logIdQ = rs.getInt("log_id");
                int sessionId = rs.getInt("session_id");
                boolean completed = rs.getBoolean("completed");

                int qId = rs.getInt("question_id");
                String text = rs.getString("text");
                String answer = rs.getString("correct_answer");
                String hint = rs.getString("hint");

                Question question = new Question(qId, text, answer, hint);
                QuestionLog qLog = new QuestionLog(sessionId, levelLogId, question, completed);
                qLog.setId(logIdQ);
                logs.add(qLog);
            }
        }
        return logs;
    }

    private void markLevelAsCompleted(Connection conn, int levelLogId) {
        String sql = "UPDATE level_log SET completed = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, levelLogId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to mark level_log as completed: " + e.getMessage());
        }
    }

    private RoomLevel createRoomLevel(String levelType, List<QuestionLog> logs) {
        return switch (levelType) {
            case "benefits"      -> new org.scrumgame.rooms.Benefits(logs);
            case "planning"      -> new org.scrumgame.rooms.Planning(logs);
            case "retrospective" -> new org.scrumgame.rooms.Retrospective(logs);
            case "scrumboard"    -> new org.scrumgame.rooms.ScrumBoard(logs);
            case "sprintreview"  -> new org.scrumgame.rooms.SprintReview(logs);
            case "daily_scrum"   -> new org.scrumgame.rooms.DailyScrum(logs);
            default -> {
                System.out.println("Onbekend level_type: " + levelType);
                yield null;
            }
        };
    }
}
