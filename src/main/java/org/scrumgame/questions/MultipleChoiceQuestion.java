package org.scrumgame.questions;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MultipleChoiceQuestion extends Question {

    public MultipleChoiceQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public MultipleChoiceQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    public String getQuestion() {
        String[] otherOptions = IncorrectAnswers();
        return MultipleChoice(this.question, this.answer, otherOptions);
    }

    public String MultipleChoice(String questionText, String correctAnswer, String[] otherOptions) {

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.addAll(Arrays.asList(otherOptions));

        Collections.shuffle(options);

        StringBuilder questionWithChoices = new StringBuilder(questionText + "\n");
        char optionLabel = 'A';
        for (String option : options) {
            questionWithChoices.append(optionLabel).append(". ").append(option).append("\n");
            optionLabel++;
        }

        questionWithChoices.append("\nPlease select the correct option (A, B, C or D):");

        return questionWithChoices.toString();
    }

    public String[] IncorrectAnswers() {
        List<String> incorrectAnswers = new ArrayList<>();

        String sql = "SELECT correct_answer FROM question WHERE correct_answer != ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, this.answer);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    incorrectAnswers.add(resultSet.getString("correct_answer"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }

        Collections.shuffle(incorrectAnswers);
        return incorrectAnswers.stream().limit(3).toArray(String[]::new);
    }
}