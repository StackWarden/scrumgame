package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MonsterLogStrategy implements LogStrategy {

    @Override
    public void log(Session session, Level level) {
        Monster monster = (Monster) level;
        MonsterLog log = new MonsterLog(session.getId(), monster.getQuestions(3), monster.isDefeated());
    }

    @Override
    public List<MonsterLog> getLogs(Session session) {
        List<MonsterLog> monsterLogs = new ArrayList<>();
        int sessionId = session.getId();

        String monsterLogSql = "SELECT id, defeated FROM monster_log WHERE session_id = ?";
        String questionIdSql = "SELECT question_id FROM monster_log_questions WHERE monster_log_id = ?";
        String questionSql = "SELECT id, text, correct_answer FROM question WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement monsterLogStatement = connection.prepareStatement(monsterLogSql)) {

            monsterLogStatement.setInt(1, sessionId);
            ResultSet monsterLogResults = monsterLogStatement.executeQuery();

            while (monsterLogResults.next()) {
                int monsterLogId = monsterLogResults.getInt("id");
                boolean isDefeated = monsterLogResults.getBoolean("defeated");

                List<Question> questions = getQuestionsForMonsterLog(
                        connection,
                        monsterLogId,
                        questionIdSql,
                        questionSql
                );

                monsterLogs.add(new MonsterLog(sessionId, questions, isDefeated));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return monsterLogs;
    }

    private List<Question> getQuestionsForMonsterLog(Connection connection, int monsterLogId, String questionIdSql, String questionSql) throws SQLException {
        List<Question> questions = new ArrayList<>();

        try (PreparedStatement questionIdStatement = connection.prepareStatement(questionIdSql)) {
            questionIdStatement.setInt(1, monsterLogId);
            ResultSet questionIdResults = questionIdStatement.executeQuery();

            while (questionIdResults.next()) {
                int questionId = questionIdResults.getInt("question_id");
                Question question = fetchQuestionById(connection, questionId, questionSql);
                questions.add(question);
            }
        }

        return questions;
    }

    private Question fetchQuestionById(Connection connection, int questionId, String questionSql) throws SQLException {
        try (PreparedStatement questionStatement = connection.prepareStatement(questionSql)) {
            questionStatement.setInt(1, questionId);
            ResultSet questionResult = questionStatement.executeQuery();

            if (questionResult.next()) {
                int id = questionResult.getInt("id");
                String text = questionResult.getString("text");
                String correctAnswer = questionResult.getString("correct_answer");

                return new Question(id, text, correctAnswer);
            } else {
                throw new SQLException("Question not found for ID: " + questionId);
            }
        }
    }
}
