package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.util.Random;

public class RiddleQuestion extends Question {

    public RiddleQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public RiddleQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    public String getQuestion() {
        return "Fill in the Blank: " + this.question + "\n" + generateFillInTheBlank(this.answer);
    }

    private String generateFillInTheBlank(String questionText) {
        String[] words = questionText.split(" ");
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        double revealRatio = 0.5;

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
