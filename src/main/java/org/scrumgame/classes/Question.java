package org.scrumgame.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Question {
    protected int id;
    protected String question;
    protected String answer;
    private static final String SELECT_QUESTION_BY_ID_SQL =
            "SELECT id, text, correct_answer FROM question WHERE id = ?";

    public Question(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
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

    // Template method
    public final boolean validateAnswer(String givenAnswer) {
        if (givenAnswer == null || answer == null) return false;
        givenAnswer = preProcessAnswer(givenAnswer);
        boolean result = checkAnswer(givenAnswer);
        postProcessAnswer(result);
        return result;
    }

    // Abstract method that must be implemented by concrete classes
    protected abstract boolean checkAnswer(String givenAnswer);

    // Hook methods that can be overridden by subclasses
    protected String preProcessAnswer(String givenAnswer) {
        // Default implementation does nothing
        return givenAnswer;
    }

    protected void postProcessAnswer(boolean result) {
        // Default implementation does nothing
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

    public static Question fetchQuestionById(Connection connection, int questionId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id, text, correct_answer, type FROM question WHERE id = ?")) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                int id = rs.getInt("id");
                String text = rs.getString("text");
                String correctAnswer = rs.getString("correct_answer");

                return createQuestion(type != null ? type : "open", id, text, correctAnswer);
            } else {
                throw new SQLException("Question not found for ID: " + questionId);
            }
        }
    }

    // Factory method to create specific question types
    public static Question createQuestion(String type, int id, String question, String answer) {
        return switch (type.toLowerCase()) {
            case "puzzle" -> new PuzzleQuestion(id, question, answer);
            case "multiple" -> new MultipleChoiceQuestion(id, question, answer);
            case "open" -> new OpenQuestion(id, question, answer);
            default -> throw new IllegalArgumentException("Unknown question type: " + type);
        };
    }

    @Override
    public String toString() {
        return String.format("Question(id=%d, question='%s')", id, question);
    }
}