package org.scrumgame.commands;

import org.scrumgame.game.GameService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Status {

    private final GameService gameService;

    public Status(GameService gameService) {
        this.gameService = gameService;
    }

    @ShellMethod(key = "status", value = "Show the status of the game")
    public String status() {
        return "You are in room " + gameService.getCurrentRoom();
    }
}
