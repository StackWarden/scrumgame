package org.scrumgame.classes;

public class Monster extends Level {
    private final String name;
    private final String question;
    private final String answer;
    private final String hint;
    private boolean defeated;
    private final Question questionObject;

    public Monster(Question question) {
        this.questionObject = question;
        this.hint = question.getHint();
        this.name = question.getQuestion();
        this.question = question.getQuestion();
        this.answer = question.getAnswer();
        this.defeated = false;
    }

    public String getName() {
        return name;
    }

    public String getHint() {
        return hint;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    public Question getQuestionObject() {
        return questionObject;
    }

    @Override
    public String getPrompt() {
        return question;
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null || answer == null) return false;
        String trimmed = userAnswer.trim();
        if (trimmed.isEmpty()) return false;
        try {
            double expected = Double.parseDouble(answer);
            double actual = Double.parseDouble(trimmed);
            return Math.abs(expected - actual) < 1e-9;
        } catch (NumberFormatException e) {
            return answer.equalsIgnoreCase(trimmed);
        }
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public Question getQuestion() {
        return new Question(-1, question, answer, hint);
    }
}