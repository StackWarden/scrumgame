package org.scrumgame.questions;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OpenQuestion extends Question {

    @Override
    protected Question Question(Connection connection, int questionId) {
        try {
            String sql = "SELECT type FROM questions WHERE type = Open";
            try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)){
                return Question.fetchQuestionById(connection, questionId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch open question.", e);
        }
    }

    @Override
    protected void displayQuestion(Question Question) {
        System.out.println("Question: " + getQuestion());

    }

    @Override
    protected void displayAnswer(Question Question) {
        System.out.println("Correct Answer: " + getAnswer());
    }
}
