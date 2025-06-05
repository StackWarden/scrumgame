package org.scrumgame.services;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.strategies.MonsterLogStrategy;
import org.scrumgame.strategies.QuestionLogStrategy;
import org.scrumgame.interfaces.GameLog;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class QuestionService {

    public static List<Question> generateQuestions(Session session, int amount) {
        Set<Integer> usedIds = getUsedQuestionIds(session);
        List<Question> unused = getUnusedQuestions(usedIds, amount);

        if (unused.size() < amount) {
            Set<Integer> alreadyAdded = unused.stream()
                    .map(Question::getId)
                    .collect(Collectors.toSet());

            List<Question> fallback = getDistinctPastQuestions(session, usedIds, alreadyAdded, amount - unused.size());
            unused.addAll(fallback);
        }

        return unused;
    }

    private static Set<Integer> getUsedQuestionIds(Session session) {
        Set<Integer> used = new HashSet<>();

        String sql = "SELECT question_id FROM question_log WHERE session_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, session.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    used.add(rs.getInt("question_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return used;
    }

    private static List<Question> getUnusedQuestions(Set<Integer> excludeIds, int limit) {
        List<Question> results = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT id, text, correct_answer, hint FROM question");

        if (!excludeIds.isEmpty()) {
            String placeholders = excludeIds.stream().map(id -> "?").collect(Collectors.joining(", "));
            query.append(" WHERE id NOT IN (").append(placeholders).append(")");
        }

        query.append(" LIMIT ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            for (Integer id : excludeIds) {
                stmt.setInt(index++, id);
            }

            stmt.setInt(index, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("correct_answer"),
                        rs.getString("hint")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    private static List<Question> getDistinctPastQuestions(Session session, Set<Integer> alreadyUsed, Set<Integer> alreadyAdded, int needed) {
        List<Question> fallback = new ArrayList<>();
        Set<Integer> added = new HashSet<>();

        LogService monsterService = new LogService();
        monsterService.setStrategy(new MonsterLogStrategy());
        for (GameLog log : monsterService.getLogs(session)) {
            if (log instanceof MonsterLog monsterLog) {
                for (Question q : monsterLog.getQuestions()) {
                    if (added.add(q.getId()) && !alreadyUsed.contains(q.getId()) && !alreadyAdded.contains(q.getId()) && fallback.size() < needed) {
                        fallback.add(q);
                    }
                }
            }
        }

        String sql = "SELECT DISTINCT q.id, q.text, q.correct_answer, q.hint " +
                "FROM question_log ql " +
                "JOIN question q ON ql.question_id = q.id " +
                "WHERE ql.session_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, session.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next() && fallback.size() < needed) {
                    int id = rs.getInt("id");
                    if (added.contains(id) || alreadyUsed.contains(id) || alreadyAdded.contains(id)) {
                        continue;
                    }

                    Question q = new Question(
                            id,
                            rs.getString("text"),
                            rs.getString("correct_answer"),
                            rs.getString("hint")
                    );
                    fallback.add(q);
                    added.add(id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fallback;
    }
}
