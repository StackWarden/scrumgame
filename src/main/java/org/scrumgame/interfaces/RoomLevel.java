package org.scrumgame.interfaces;

import org.scrumgame.classes.Question;

import java.util.Queue;

public interface RoomLevel {
    Integer getRoomNumber();
    Queue<Question> getRemainingQuestions();
}