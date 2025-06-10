package org.scrumgame.assistant;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class AssistantCommands {

    private final AssistantActivator activator;

    public AssistantCommands(AssistantActivator activator) {
        this.activator = activator;
    }

    @ShellMethod(key = "jarvis", value = "Activate the assistant in this room.")
    public void runAssistant() {
        activator.run();
    }
}
