package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.util.Random;

public class RiddleQuestion extends Question {
    private String fillIn;

    public RiddleQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public RiddleQuestion(Question question, String answer) {
        super(question.getQuestion(), answer);
    }

    @Override
    public String getQuestion() {
        return "Fill in the Blank: " + this.question + "\n" + generateFillInTheBlank(this.getAnswer());
    }
    

    private String generateFillInTheBlank(String questionText) {
        String[] words = questionText.split(" ");
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        double revealRatio = words.length > 1 ? 0.625 : 0.01;

        for (String word : words) {
            if (random.nextDouble() < revealRatio) {
                result.append(word).append(" ");
            } else {
                result.append("_".repeat(word.length())).append(" ");
            }
        }

        if (fillIn == null || !fillIn.contains("_")) {
            setFillIn(result.toString().trim());
        }

        return getFillIn();
    }

    public void setFillIn(String fillIn) {
        this.fillIn = fillIn;
    }

    public String getFillIn() {
        return fillIn;
    }
}