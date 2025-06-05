package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.LogStrategy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionLogStrategy implements LogStrategy {

    private static final String INSERT_SQL =
            "INSERT INTO question_log (session_id, question_id, completed) VALUES (?, ?, ?)";
    private static final String SELECT_SQL =
            "SELECT id, session_id, question_id, completed, level_log_id FROM question_log WHERE session_id = ?";
    private int lastInsertedLogId = -1;

    @Override
    public Level log(Session session, Level level) {
        Room room = (Room) level;
        Question question = room.getQuestion();

        if (question == null) {
            throw new IllegalStateException("No question to log.");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, session.getId());
            stmt.setInt(2, question.getId());
            stmt.setInt(3, room.getLogId());
            stmt.setBoolean(4, false);

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int logId = keys.getInt(1);
                room.setLogId(logId);
                lastInsertedLogId = logId;
            }
            return room;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<QuestionLog> getLogs(Session session) {
        List<QuestionLog> questionLogs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_SQL)) {

            stmt.setInt(1, session.getId());
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                int logId = result.getInt("id");
                int questionId = result.getInt("question_id");
                boolean completed = result.getBoolean("completed");
                int levelLogId = result.getInt("level_log_id");

                Question question = Question.fetchQuestionById(connection, questionId);
                QuestionLog log = new QuestionLog(session.getId(),levelLogId, question, completed);
                log.setId(logId);

                questionLogs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questionLogs;
    }

    @Override
    public void markCurrentLogCompleted(Session session) {
        int logId = session.getCurrentQuestionLogId();

        if (logId == -1) {
            return;
        }

        String sql = "UPDATE question_log SET completed = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, logId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("[WARNING] No question_log row found with id = " + logId);
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to update question_log for id = " + logId);
            e.printStackTrace();
        }
    }

    @Override
    public String getPromptByLogId(int logId) {
        String sql = "SELECT question_id FROM question_log WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int qId = rs.getInt("question_id");
                Question q = Question.fetchQuestionById(conn, qId);
                return q.getQuestion();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No prompt found.";
    }

    @Override
    public Level loadByLogId(int logId) {
        String sql = "SELECT question_id FROM question_log WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int qId = rs.getInt("question_id");
                Question q = Question.fetchQuestionById(conn, qId);
                Room room = new Room(q);
                room.setLogId(logId);
                return room;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLastInsertedLogId() {
        return lastInsertedLogId;
    }
}
