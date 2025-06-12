package org.scrumgame.repositories;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.Session;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DatabaseQuestionRepository implements QuestionRepository {
    @Override
    public Set<Integer> findUsedQuestionIds(Session session) {
        Set<Integer> usedIds = new HashSet<>();
        String sql = "SELECT question_id FROM question_log WHERE session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usedIds.add(rs.getInt("question_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching used question IDs", e);
        }
        return usedIds;
    }

    @Override
    public List<Question> findNewQuestions(Set<Integer> excludeIds, int limit) {
        List<Question> questions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, text, correct_answer, hint FROM question"
        );
        if (!excludeIds.isEmpty()) {
            String placeholders = String.join(", ", Collections.nCopies(excludeIds.size(), "?"));
            sql.append(" WHERE id NOT IN (").append(placeholders).append(")");
        }
        sql.append(" LIMIT ?");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Integer id : excludeIds) {
                stmt.setInt(idx++, id);
            }
            stmt.setInt(idx, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(new Question(
                            rs.getInt("id"),
                            rs.getString("text"),
                            rs.getString("correct_answer"),
                            rs.getString("hint")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching new questions", e);
        }
        return questions;
    }

    @Override
    public List<Question> findHistoricQuestions(Session session, Set<Integer> excludeIds, int limit) {
        List<Question> historic = new ArrayList<>();
        String sql = "SELECT DISTINCT q.id, q.text, q.correct_answer, q.hint " +
                "FROM question_log ql JOIN question q ON ql.question_id = q.id " +
                "WHERE ql.session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next() && historic.size() < limit) {
                    int id = rs.getInt("id");
                    if (!excludeIds.contains(id)) {
                        historic.add(new Question(
                                id,
                                rs.getString("text"),
                                rs.getString("correct_answer"),
                                rs.getString("hint")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching historic questions", e);
        }
        return historic;
    }
}