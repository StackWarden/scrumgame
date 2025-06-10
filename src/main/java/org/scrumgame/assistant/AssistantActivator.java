package org.scrumgame.assistant;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssistantActivator {
    private final List<AssistantAction> actions;

    public AssistantActivator(List<AssistantAction> actions) {
        this.actions = actions;
    }

    public void run() {
        for (AssistantAction action : actions) {
            action.run();
        }
    }
}
