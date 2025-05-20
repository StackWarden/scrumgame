package org.scrumgame.commands;

import org.scrumgame.game.GameService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class MenuCommands {

    private final GameService gameService;

    public MenuCommands(GameService gameService) {
        this.gameService = gameService;
    }

    @ShellMethod(key = "start", value = "Start a new game.")
    @ShellMethodAvailability("menuAvailable")
    public String startGame() {
        gameService.startNewSession();
        return "Game started. " + gameService.getCurrentPrompt();
    }

    @ShellMethod(key = "load-game", value = "Load a saved game.")
    @ShellMethodAvailability("menuAvailable")
    public String loadGame(@ShellOption int slot) {
        gameService.loadSession(slot);
        return "Game loaded.";
    }

    public Availability menuAvailable() {
        return !gameService.isInGame()
                ? Availability.available()
                : Availability.unavailable("You are already in a game.");
    }
}
