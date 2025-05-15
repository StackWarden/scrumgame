package org.scrumgame.database.models;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Session {
    private int id;
    private int playerId;
    private int currentLevel;
    private Optional<Integer> currentMonster = Optional.empty();    private int score;
    private int monstersEncountered;
    private boolean gameOver;

    // Constructor with ID (used when loading from DB)
    public Session(int id, int playerId, int currentLevel, int score, int monstersEncountered, boolean gameOver) {
        this.id = id;
        this.playerId = playerId;
        this.currentLevel = currentLevel;
        this.score = score;
        this.monstersEncountered = monstersEncountered;
        this.gameOver = gameOver;
    }

    public Session(boolean gameOver, int monstersEncountered, int score, int currentLevel, int playerId) {
        this(0, playerId, currentLevel, score, monstersEncountered, gameOver);
    }

    public static Session getSessionById(int sessionId) {
        String query = """
            SELECT id, player_id, current_level_log_id, score, monster_encounters, gameover
            FROM session
            WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                int playerId = rs.getInt("player_id");
                int currentLevel = rs.getInt("current_level_log_id");
                int score = rs.getInt("score");
                int monstersEncountered = rs.getInt("monster_encounters");
                boolean gameOver = rs.getBoolean("gameover");

                return new Session(id, playerId, currentLevel, score, monstersEncountered, gameOver);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentMonsterId() {
        return currentMonster.orElse(0);
    }

    public int getScore() {
        return score;
    }

    public int getMonstersEncountered() {
        return monstersEncountered;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void finishMonsterFight(boolean defeated) {
        int newEncounters = monstersEncountered + 1;
        int newScore = defeated ? score + 10 : score;

        String sql = "UPDATE session SET monster_encounters = ?, current_monster_log_id = NULL, score = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newEncounters);
            stmt.setInt(2, newScore);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            Session updated = getSessionById(id);
            this.monstersEncountered = updated.monstersEncountered;
            this.score = updated.score;
            this.currentMonster = Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentMonsterLogId(int logId) {
        String sql = "UPDATE session SET current_monster_log_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, logId);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            this.currentMonster = Optional.of(logId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markGameOver() {
        String sql = "UPDATE session SET gameover = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            this.gameOver = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRoom (int roomLogId) {
        currentLevel = roomLogId;
    }

    public Room getCurrentRoom() {
        if (currentLevel == 0) return null;

        String query = """
        SELECT question_id, completed
        FROM level_log
        WHERE id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentLevel);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int questionId = rs.getInt("question_id");
                boolean completed = rs.getBoolean("completed");

                Question question = fetchQuestionById(conn, questionId);
                return new Room(question, completed);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Monster getCurrentMonster() {
        if (currentMonster.isEmpty()) return null;

        String queryLog = "SELECT id FROM monster_log WHERE id = ?";
        String queryQuestionIds = "SELECT question_id FROM monster_log_questions WHERE monster_log_id = ?";

        List<Question> questions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement logStmt = conn.prepareStatement(queryLog)) {

            logStmt.setInt(1, currentMonster.get());
            ResultSet logRs = logStmt.executeQuery();

            if (!logRs.next()) return null;

            try (PreparedStatement qidStmt = conn.prepareStatement(queryQuestionIds)) {
                qidStmt.setInt(1, currentMonster.get());
                ResultSet qidRs = qidStmt.executeQuery();

                while (qidRs.next()) {
                    int qId = qidRs.getInt("question_id");
                    questions.add(fetchQuestionById(conn, qId));
                }
            }

            Monster monster = new Monster();
            monster.setQuestions(questions);
            return monster;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Question fetchQuestionById(Connection conn, int questionId) throws SQLException {
        String sql = "SELECT id, text, correct_answer FROM question WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("correct_answer")
                );
            } else {
                throw new SQLException("No question found for ID: " + questionId);
            }
        }
    }

    public static Session createNewSession() {
        String sql = """
        INSERT INTO session (player_id, current_level_log_id, score, monster_encounters, gameover)
        VALUES (?, NULL, 0, 0, false)
    """;

        int playerId = 1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, playerId);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                return getSessionById(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void completeCurrentRoom () {
        score++;
        currentLevel = 0;
    }
}