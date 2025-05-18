package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.QuestionService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonsterLogStrategy implements LogStrategy {

    private static final String INSERT_MONSTER_LOG_SQL =
            "INSERT INTO monster_log (session_id, defeated) VALUES (?, ?)";
    private static final String INSERT_MONSTER_LOG_QUESTION_SQL =
            "INSERT INTO monster_log_questions (monster_log_id, question_id) VALUES (?, ?)";
    private static final String SELECT_MONSTER_LOGS_SQL =
            "SELECT id, defeated FROM monster_log WHERE session_id = ?";
    private static final String SELECT_LOG_QUESTION_IDS_SQL =
            "SELECT question_id FROM monster_log_questions WHERE monster_log_id = ?";
    private static final String SELECT_QUESTION_BY_ID_SQL =
            "SELECT id, text, correct_answer FROM question WHERE id = ?";

    private int lastInsertedLogId = -1;


    @Override
    public Level log(Session session, Level level) {
        Monster monster = (Monster) level;
        Question question = monster.getQuestionObject();
        boolean defeated = monster.isDefeated();

        if (question == null || question.getId() <= 0) {
            System.err.println("[ERROR] Cannot log monster: question is invalid or not stored in DB.");
            return monster;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertLog = "INSERT INTO monster_log (session_id, defeated) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertLog, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, session.getId());
                stmt.setBoolean(2, defeated);
                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int logId = keys.getInt(1);
                    monster.setLogId(logId);
                }

                String insertQuestion = "INSERT INTO monster_log_questions (monster_log_id, question_id) VALUES (?, ?)";
                try (PreparedStatement qStmt = conn.prepareStatement(insertQuestion)) {
                    qStmt.setInt(1, monster.getLogId());
                    qStmt.setInt(2, question.getId());
                    qStmt.executeUpdate();
                }
                return monster;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MonsterLog> getLogs(Session session) {
        List<MonsterLog> monsterLogs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement logStmt = conn.prepareStatement(SELECT_MONSTER_LOGS_SQL)) {

            logStmt.setInt(1, session.getId());
            ResultSet logResults = logStmt.executeQuery();

            while (logResults.next()) {
                int logId = logResults.getInt("id");
                boolean defeated = logResults.getBoolean("defeated");
                List<Question> questions = getQuestionsForLog(conn, logId);
                monsterLogs.add(new MonsterLog(logId, session.getId(), questions, defeated));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monsterLogs;
    }

    @Override
    public void markCurrentLogCompleted(Session session) {
        String sql = "UPDATE monster_log SET defeated = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getCurrentRoomId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Question> getQuestionsForLog(Connection conn, int logId) throws SQLException {
        List<Question> questions = new ArrayList<>();

        try (PreparedStatement idStmt = conn.prepareStatement(SELECT_LOG_QUESTION_IDS_SQL)) {
            idStmt.setInt(1, logId);
            ResultSet idRs = idStmt.executeQuery();

            while (idRs.next()) {
                int questionId = idRs.getInt("question_id");
                questions.add(fetchQuestionById(conn, questionId));
            }
        }

        return questions;
    }

    private Question fetchQuestionById(Connection conn, int questionId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_QUESTION_BY_ID_SQL)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("correct_answer")
                ) {
                    @Override
                    protected boolean checkAnswer(String givenAnswer) {
                        return false;
                    }
                };
            }

            throw new SQLException("Question not found for ID: " + questionId);
        }
    }

    @Override
    public String getPromptByLogId(int logId) {
        String qSql = "SELECT question_id FROM monster_log_questions WHERE monster_log_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(qSql)) {
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int qId = rs.getInt("question_id");
                Question q = fetchQuestionById(conn, qId);
                return q.getQuestion();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No monster prompt found.";
    }

    @Override
    public Level loadByLogId(int logId) {
        String qSql = "SELECT question_id FROM monster_log_questions WHERE monster_log_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(qSql)) {
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int questionId = rs.getInt("question_id");
                Question q = Question.fetchQuestionById(conn, questionId);
                return new Monster(q);
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
