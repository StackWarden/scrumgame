package org.scrumgame.rooms;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Question;
import org.scrumgame.interfaces.RoomLevel;

import java.util.LinkedList;
import java.util.Queue;

public class Benefits extends Level implements RoomLevel {

    private final Queue<Question> questions = new LinkedList<>();
    private Question currentQuestion;

    public Benefits(Queue<Question> questions) {
        this.questions.addAll(questions);
        this.currentQuestion = this.questions.peek();
        setRoomNumber(1);
    }

    @Override
    public String getPrompt() {
        return currentQuestion != null ? currentQuestion.getQuestion() : "All questions answered!";
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (currentQuestion == null) return false;

        boolean correct = currentQuestion.getAnswer().trim().equalsIgnoreCase(answer.trim());
        if (correct) {
            questions.poll(); // verwijder huidige vraag
            currentQuestion = questions.peek(); // volgende vraag klaarzetten
        }
        return correct;
    }

    @Override
    public String getAnswer() {
        return currentQuestion != null ? currentQuestion.getAnswer() : "No more questions.";
    }

    @Override
    public Question getQuestion() {
        return currentQuestion;
    }

    public boolean isCompleted() {
        return questions.isEmpty();
    }

    public Queue<Question> getRemainingQuestions() {
        return new LinkedList<>(questions);
    }
}