package org.scrumgame.interfaces;

import org.scrumgame.classes.Question;

import java.util.List;

public interface GameLog {
    int sessionId();
    List<Question> questions();
}