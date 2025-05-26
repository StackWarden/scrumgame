package org.scrumgame.jokers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class JokerCommand {

    private final JokerService jokerService;

    @Autowired
    public JokerCommand(JokerService jokerService) {
        this.jokerService = jokerService;
    }

    @ShellMethod(key = "use-joker", value = "Use a joker by ID (e.g., joker1, joker2).")
    public String useJoker(@ShellOption(help = "The joker ID to use") String jokerId) {
        return jokerService.useJoker(jokerId);
    }
}
