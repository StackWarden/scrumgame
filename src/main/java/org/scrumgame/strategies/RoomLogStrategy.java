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
    private static final String SELECT_QUESTION_BY_ID_SQL =
            "SELECT id, text, correct_answer FROM question WHERE id = ?";
    private int lastInsertedLogId = -1;


    @Override
    public void log(Session session, Level level) {
        Room room = (Room) level;
        Question question = room.getQuestion();

        if (question == null) {
            throw new IllegalStateException("No questions available to log for this room.");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ROOM_LOG_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, session.getId());
            stmt.setInt(2, question.getId());
            stmt.setBoolean(3, true);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                lastInsertedLogId = keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                Question question = fetchQuestionById(connection, questionId);

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
            stmt.setInt(1, session.getCurrentLevel());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Question fetchQuestionById(Connection connection, int questionId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_QUESTION_BY_ID_SQL)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("correct_answer")
                );
            } else {
                throw new SQLException("Question not found for ID: " + questionId);
            }
        }
    }

    public int getLastInsertedLogId() {
        return lastInsertedLogId;
    }
}
