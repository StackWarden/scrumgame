package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.RoomLog;
import org.scrumgame.database.models.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomLogStrategy implements LogStrategy {

    private static final String INSERT_ROOM_LOG_SQL =
            "INSERT INTO level_log (session_id, question_id, completed) VALUES (?, ?, ?)";
    private static final String SELECT_ROOM_LOGS_SQL =
            "SELECT id, question_id, completed FROM level_log WHERE session_id = ?";
    private int lastInsertedLogId = -1;


    @Override
    public Level log(Session session, Level level) {
        Room room = (Room) level;
        Question question = room.getQuestion();

        if (question == null) {
            throw new IllegalStateException("No question to log.");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO level_log (session_id, question_id, completed) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, session.getId());
            stmt.setInt(2, question.getId());
            stmt.setBoolean(3, false);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int logId = keys.getInt(1);
                room.setLogId(logId);
            }
            return room;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<RoomLog> getLogs(Session session) {
        List<RoomLog> roomLogs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement roomLogStatement = connection.prepareStatement(SELECT_ROOM_LOGS_SQL)) {

            roomLogStatement.setInt(1, session.getId());
            ResultSet result = roomLogStatement.executeQuery();

            while (result.next()) {
                int questionId = result.getInt("question_id");
                boolean completed = result.getBoolean("completed");
                Question question = Question.fetchQuestionById(connection, questionId);

                RoomLog log = new RoomLog(session.getId(), question, completed);
                roomLogs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomLogs;
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
        String sql = "SELECT question_id FROM level_log WHERE id = ?";
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
        String sql = "SELECT question_id FROM level_log WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int qId = rs.getInt("question_id");
                Question q = Question.fetchQuestionById(conn, qId);
                return new Room(q);
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
