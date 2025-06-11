package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        System.out.println("Correct Answer: " + question.getAnswer());
    }
}