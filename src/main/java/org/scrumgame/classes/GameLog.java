package org.scrumgame.classes;

import java.util.List;

public interface GameLog {
    int getSessionId();
    List<Question> getQuestions();
}