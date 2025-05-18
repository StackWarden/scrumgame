package org.scrumgame.classes;

public class PuzzleQuestion extends Question {
    
    public PuzzleQuestion(int id, String question, String answer) {
        super(id, question, answer);
    }

    public PuzzleQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    protected boolean checkAnswer(String givenAnswer) {
        // Puzzle answers might require special formatting
        String formattedAnswer = formatPuzzleAnswer(givenAnswer);
        return answer.equals(formattedAnswer);
    }

    @Override
    protected void preProcessAnswer(String givenAnswer) {
        // Remove any special characters or spaces from puzzle answers
        givenAnswer = givenAnswer.replaceAll("[^a-zA-Z0-9]", "");
    }

    private String formatPuzzleAnswer(String answer) {
        // Remove spaces and convert to lowercase for puzzle answers
        return answer.toLowerCase().replaceAll("\\s+", "");
    }
}