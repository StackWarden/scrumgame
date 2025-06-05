package org.scrumgame.interfaces;

import org.scrumgame.classes.Question;
import java.util.List;

public interface HasMultipleQuestions {
    List<Question> getRemainingQuestions();
    // room to grow: getAnsweredQuestions(), resetQuestions(), etc.
}