package org.scrumgame.classes;

import org.scrumgame.questions.TypeLessQuestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Question {
    private String hint;
    private int id;
    private String question;
    private String answer;
    private String type;
    private static final String SELECT_QUESTION_BY_ID_SQL =
            "SELECT id, text, correct_answer, hint, type FROM question WHERE id = ?";

    public Question(int id, String question, String answer, String hint) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
    }

    public Question(int id, String question, String answer, String hint, String type) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.type = type;
    }

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean checkAnswer(String givenAnswer) {
        return answer.trim().equalsIgnoreCase(givenAnswer.trim());
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public static Question fetchQuestionById(Connection connection, int questionId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_QUESTION_BY_ID_SQL)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TypeLessQuestion(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("correct_answer"),
                        rs.getString("hint")
                );
            } else {
                throw new SQLException("Question not found for ID: " + questionId);
            }
        }
    }

}
