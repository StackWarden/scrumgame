package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class RiddleQuestion extends Question {

    public RiddleQuestion(int id, String question, String answer, String hint, String type) {
        super(id, question, answer, hint, type);
    }

    @Override
    protected Question fetchQuestion(Connection connection, int questionId) {
        try {
            String sql = "SELECT type FROM questions WHERE type = 'Riddle'";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                return Question.fetchQuestionById(connection, questionId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch riddle question.", e);
        }
    }

    @Override
    protected void displayQuestion(Question question) {
        System.out.println("Question: " + question.getQuestion());
    }

    @Override
    protected void displayAnswer(Question question) {
        System.out.println("Correct Answer: " + generateFillInTheBlank(question.getQuestion()));
    }

    private String generateFillInTheBlank(String questionText) {
        String[] words = questionText.split(" ");
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        double revealRatio = 0.6;

        for (String word : words) {
            if (random.nextDouble() < revealRatio) {
                result.append(word).append(" ");
            } else {
                result.append("_".repeat(word.length())).append(" ");
            }
        }

        return result.toString().trim();
    }

}