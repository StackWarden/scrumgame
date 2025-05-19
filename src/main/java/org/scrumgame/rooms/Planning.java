package org.scrumgame.rooms;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Room;

public class Planning extends Level {

    @Override
    public String getPrompt() {
        return "";
    }

    @Override
    public boolean checkAnswer(String answer) {
        return false;
    }

    @Override
    public String getAnswer() {
        return "";
    }
}
