package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.RoomLog;
import org.scrumgame.database.models.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomLogStrategy implements LogStrategy {

    @Override
    public void log(Session session, Level level) {
        Room room = (Room) level;
        RoomLog log = new RoomLog(session.getId(), room.getQuestions().getFirst(), true);
        // Save `log` to database if needed
    }

    @Override
    public List<RoomLog> getLogs(Session session) {
        List<RoomLog> roomLogs = new ArrayList<>();
        int sessionId = session.getId();

        String roomLogSql = "SELECT id, question_id, completed FROM level_log WHERE session_id = ?";
        String questionSql = "SELECT id, text, correct_answer FROM question WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement roomLogStatement = connection.prepareStatement(roomLogSql)) {

            roomLogStatement.setInt(1, sessionId);
            ResultSet roomLogResults = roomLogStatement.executeQuery();

            while (roomLogResults.next()) {
                int questionId = roomLogResults.getInt("question_id");
                boolean completed = roomLogResults.getBoolean("completed");

                Question question = fetchQuestionById(connection, questionId, questionSql);

                RoomLog log = new RoomLog(sessionId, question, completed);
                roomLogs.add(log);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return roomLogs;
    }

    private Question fetchQuestionById(Connection connection, int questionId, String query) throws SQLException {
        try (PreparedStatement questionStatement = connection.prepareStatement(query)) {
            questionStatement.setInt(1, questionId);
            ResultSet result = questionStatement.executeQuery();

            if (result.next()) {
                int id = result.getInt("id");
                String text = result.getString("text");
                String correctAnswer = result.getString("correct_answer");
                return new Question(id, text, correctAnswer);
            } else {
                throw new SQLException("Question not found for ID: " + questionId);
            }
        }
    }
}