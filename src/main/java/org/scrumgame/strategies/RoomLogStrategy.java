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
            e.printStackTrace();
        }
    }

    @Override
    public String getPromptByLogId(int logId) {
        RoomLevel level = (RoomLevel) loadByLogId(logId);
        return level != null ? level.getPrompt() : "Geen prompt gevonden.";
    }

    @Override
    public Level loadByLogId(int logId) {
        String levelSql = "SELECT room_number, level_type FROM level_log WHERE id = ?";
        String questionSql = """
        SELECT ql.id AS log_id, ql.session_id, ql.completed, ql.level_log_id,
               q.id AS question_id, q.text, q.correct_answer, q.hint
        FROM question_log ql
        JOIN question q ON ql.question_id = q.id
        WHERE ql.level_log_id = ?
        ORDER BY ql.id ASC
    """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            int roomNumber;
            String levelType;

            try (PreparedStatement stmt = conn.prepareStatement(levelSql)) {
                stmt.setInt(1, logId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) return null;

                roomNumber = rs.getInt("room_number");
                levelType = rs.getString("level_type");
            }

            List<QuestionLog> logs = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(questionSql)) {
                stmt.setInt(1, logId);
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
                    QuestionLog qLog = new QuestionLog(sessionId, logId, question, completed);
                    qLog.setId(logIdQ);
                    logs.add(qLog);
                }
            }

            RoomLevel level;
            switch (levelType) {
                case "benefits" -> level = new Benefits(logs);
                default -> {
                    System.out.println("Onbekend level_type: " + levelType);
                    return null;
                }
            }

            level.setLogId(logId);
            level.setRoomNumber(roomNumber);
            return (Level) level;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
