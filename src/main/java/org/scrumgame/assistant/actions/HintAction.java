package org.scrumgame.assistant.actions;

import org.scrumgame.assistant.AssistantAction;

public class HintAction implements AssistantAction {
    @Override
    public void run() {
        System.out.println("Hint action is running.");
    }
}
