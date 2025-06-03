package org.scrumgame.assistant;

import org.scrumgame.assistant.actions.HintAction;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

@ShellComponent
public class AssistantCommands {

    @ShellMethod(key = "jarvis" , value = "Activate the assistant in this room.")
    public void runAssistant() {
        AssistantActivator activator = new AssistantActivator(List.of(
                new HintAction()
        ));

        activator.run();
    }
}
